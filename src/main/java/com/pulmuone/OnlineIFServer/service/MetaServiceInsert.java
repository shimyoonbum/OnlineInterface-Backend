package com.pulmuone.OnlineIFServer.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.pulmuone.OnlineIFServer.common.ConnectionCommon;
import com.pulmuone.OnlineIFServer.common.Constants;
import com.pulmuone.OnlineIFServer.common.IFException;
import com.pulmuone.OnlineIFServer.common.ResponseStatus;
import com.pulmuone.OnlineIFServer.config.MetaConfig;
import com.pulmuone.OnlineIFServer.util.CUtil;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class MetaServiceInsert {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    //Temporary
    private final static String restrictTemp = "restrict";
    private final static String batchListTemp = "batchList";

    @Autowired
    JdbcTemplate jdbcTemplate;

	public String[] insertChildMap(Map metaMap, Map<String, Object> commonMap, Map<String, Object> paramMap) throws IFException {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		Connection connStack = null;														

		ConnectionCommon connect = ConnectionCommon.getInstance();							
		
		getSqlPart(metaMap, commonMap, paramMap, resultMap);		
		
		try {
			connStack = connect.beginTransaction(jdbcTemplate);			
			executeChildData(connect, connStack , metaMap, commonMap, resultMap);
			connect.endTransaction(connStack, true);
			
		} catch(Exception e) {
			connect.endTransaction(connStack,false);
        	throw new IFException(ResponseStatus.FAIL, e.getMessage().replaceAll("\n", "").replaceAll("\"", "'"));			
		} finally {
			if(connStack != null ){
				connStack = null;
			}
		}
		
		resultMap.put("responseCode", ResponseStatus.OK.value());
		resultMap.put("responseMessage", ResponseStatus.OK.phrase());
		
		return new String[] {ResponseStatus.OK.value(), new Gson().toJson(resultMap)};
	}

	private void getSqlPart(Map metaMap, Map<String, Object> commonMap, Map<String, Object> paramMap, Map<String,Object> resultMap) throws IFException {
		List<Map> paramList = (List<Map>) paramMap.get(metaMap.get(Constants.path).toString());
		
		for(Map param : paramList) {
			buildSqlPart(metaMap, commonMap, param, resultMap);
			
	    	List<Map> children = (List<Map>) metaMap.get(MetaConfig.childrenMap);
			if(children == null)
	    		continue;
			
			for(Map child : children) {
				getSqlPart(child, commonMap, param, resultMap);
			}
		}
	}

	private void buildSqlPart(Map metaMap, Map<String, Object> commonMap, 
			Map<String, Object> paramMap, Map<String,Object> resultMap) throws IFException {
		
		String path = metaMap.get(Constants.path).toString();
		String systemId = commonMap.get(Constants.systemId).toString();
		
		String restrict = (metaMap.get(restrictTemp) == null)? null : metaMap.get(restrictTemp).toString();
		String restrictColumn = null;
		List<String> restrictValues = new ArrayList<String>();
		boolean needRestrict = false;									//데이터 접근제한이 필요한지 플래그
		
		if(restrict!=null) {
			String[] restricts = restrict.split(":");
			String[] systems = restricts[0].split(",");
			List<String> restrictSystems = new ArrayList<String>();
			for(String s : systems)
				restrictSystems.add(s.trim());
			String[] restrictCondition = restricts[1].split("=");
			restrictColumn = restrictCondition[0].trim().toLowerCase();
			String[] values = restrictCondition[1].split(",");
			for(String v : values)
				restrictValues.add(v.trim());
			
			if(restrictSystems.contains(systemId))
				needRestrict = true;
		}
		Map sqlMap = (Map) resultMap.get(path);
		if(sqlMap==null) {
			sqlMap = new HashMap<String,Object>();
			resultMap.put(path, sqlMap);
		}

		List<List> batchList = (List<List>) sqlMap.get(batchListTemp);
		if(batchList==null) {
			batchList = new ArrayList<List>();
			sqlMap.put(batchListTemp, batchList);
		}
    	
		List<Object> valList = new ArrayList<Object>();

		List<Map> metaAttrs = (List<Map>) metaMap.get(MetaConfig.ifCols);
		if(metaAttrs==null)
			throw new IFException(ResponseStatus.FAIL, path+"의 ifmeta_attr정보가 없습니다.");
		
		boolean existJoinKey = false;
		boolean existJoinVal = false;
		
    	for(Map metaAttr : metaAttrs) {
    		if(metaAttr.get(Constants.mandatory)!=null && metaAttr.get(Constants.mandatory).equals(Constants.exceptMandatory))
    			continue;
    		
    		String dbCol = metaAttr.get(Constants.dbCol).toString();
    		Object val = paramMap.get(metaAttr.get(Constants.ifCol).toString());
    		valList.add(val);
    		
    		if(val==null && metaAttr.get(Constants.mandatory)!=null && metaAttr.get(Constants.mandatory).equals(Constants.systemMandatory))
				throw new IFException(ResponseStatus.MISSING, dbCol+" 값이 없습니다.");
    		if(metaAttr.get(Constants.mandatory)!=null && metaAttr.get(Constants.mandatory).equals(Constants.joinMandatory)) {
    			existJoinKey = true;
    			if(val!=null)
    				existJoinVal = true;
    		}
    		
			if(needRestrict && dbCol.equals(restrictColumn) && !restrictValues.contains(val.toString()))
				throw new IFException(ResponseStatus.ACCESS, restrictColumn+"에 입력 제한된 값("+val.toString()+")이 있습니다.");
		}
    	
    	if(existJoinKey && !existJoinVal)
    		throw new IFException(ResponseStatus.MISSING,"JOIN항목의 값이 없습니다.");
    	
		batchList.add(valList);
	}

	private void executeChildData(ConnectionCommon connect, Connection connStack, 
			Map metaMap, 
			Map<String, Object> commonMap, Map<String,Object> resultMap) throws IFException {
		String sqlMessage;
		executeInsert(connect, connStack, metaMap, commonMap, resultMap);
		
    	List<Map> children = (List<Map>) metaMap.get(MetaConfig.childrenMap);
		if(children == null)
			return;
		
		for(Map child : children) {
			executeChildData(connect, connStack, child, commonMap, resultMap);
		}
	}
	
	private void executeInsert(ConnectionCommon connect, Connection connStack, Map metaMap, Map<String, Object> commonMap, Map<String, Object> resultMap) throws IFException {
		String path = metaMap.get(Constants.path).toString();
		Map sqlMap = (Map) resultMap.get(path);
		
    	StringBuffer sbSql = new StringBuffer();
    	
		sbSql.append("insert into ");
		String ifTbl = metaMap.get(Constants.ifTbl).toString();
		sbSql.append(ifTbl);
		
		List<Map> metaAttrs = (List<Map>) metaMap.get(MetaConfig.ifCols);
		boolean needComma = false;
		sbSql.append(" (");

		
    	for(Map metaAttr : metaAttrs) {
    		if(metaAttr.get(Constants.mandatory)!=null && metaAttr.get(Constants.mandatory).equals(Constants.exceptMandatory))
    			continue;
    		
    		String dbCol = metaAttr.get(Constants.dbCol).toString();
    		if(needComma)
        		sbSql.append(",");
    		sbSql.append(dbCol);
    		needComma = true;
    	}
		sbSql.append(") ");
		
		needComma = false;
		sbSql.append("values (");		
		
    	for(Map metaAttr : metaAttrs) {
    		if(metaAttr.get(Constants.mandatory)!=null && metaAttr.get(Constants.mandatory).equals(Constants.exceptMandatory))
    			continue;

    		String inConv= (metaAttr.get(Constants.inConv) == null)? "?" : metaAttr.get(Constants.inConv).toString().replaceAll("\\$", "?");
    		if(needComma)
        		sbSql.append(",");
    		sbSql.append(inConv);
    		needComma = true;
    	}
		sbSql.append(") ");
    	
		List<List> batchList = (List<List>) sqlMap.get(batchListTemp);
		
		Map<String, Object> attrs = new HashMap<String, Object>();
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		
		for(List<Object> list : batchList) {
			Object[] oa = new Object[list.size()];	//[attrSize]
			int idx = 0;
			for(Object val : list)
				oa[idx++]= val;
			batchArgs.add(oa);
		}
		
		logger.debug(sbSql.toString()+";\n"+CUtil.convertListOfObjectArrayToJsonString(batchArgs));
		
		try {			
			connect.batchUpdate(connStack, sbSql.toString(), batchArgs);			
			resultMap.remove(path);
		} catch (Exception e) {
			throw new IFException(ResponseStatus.FAIL, e.getMessage().replaceAll("\n", "").replaceAll("\"", "'"));
		}
	}	
}
