package com.pulmuone.OnlineIFServer.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.pulmuone.OnlineIFServer.common.IFException;
import com.pulmuone.OnlineIFServer.common.ResponseStatus;
import com.pulmuone.OnlineIFServer.util.CamelListMap;

@Service
public class BackupServiceImpl implements BackupService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    JdbcTemplate jdbcTemplate;
    
    private Connection conn = null;

	public List<Map<String, Object>> selectBackup(Map<String, Object> param) {			
		return getBackupSql(param);
	}

	private List<Map<String, Object>> getBackupSql(Map<String, Object> param) {		
	    StringBuffer trigSB = new StringBuffer();
	    StringBuffer valuesSB = new StringBuffer();
	    StringBuffer valuesSB2 = new StringBuffer();
	    String colNmStr = null;
	    String colNm = null;
	    String colType = null;
	    String nullable = null;
	    
	    trigSB.append("with backup as (\n");
	    trigSB.append("select to_char(backup_date, 'yyyymmdd') backup_date,tbl_nm, REPLACE(REPLACE(REPLACE(REPLACE(json, CHR(9), ''), CHR(10), ''), CHR(13), ''), '$', '') json from ifbackup where backup_date\n");
	    trigSB.append("between to_date('"+ param.get("dateFrom").toString()+"', 'yyyymmddhh24miss') and to_date('"+ param.get("dateTo").toString()+"', 'yyyymmddhh24miss') and tbl_nm='"+ param.get("table").toString().toLowerCase() +"')\n");
	    trigSB.append(",tbl AS (\n");
	    trigSB.append("select /*+ materialize */ rownum rn, backup_date ");	///*+ materialize */ 
	    
	    String sql = "select column_name,data_type,nullable from user_tab_columns where table_name = '"+ param.get("table").toString() + "'";
		
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		for (Map<String, Object> rec : list) {
			colNm = rec.get("column_name").toString().toLowerCase();
	    	colType = rec.get("data_type").toString().toLowerCase();
	    	nullable = rec.get("nullable").toString();	    
			
			if(colType.equals("varchar2") || colType.equals("nvarchar2") || colType.equals("char") || colType.equals("nchar") || colType.equals("clob")) {
				valuesSB.append(" , t."+colNm+" " +colNm);
			} else if(colType.equals("date")) {
				valuesSB.append(", to_char(to_date("+colNm+",'yyyymmddhh24miss'),'yyyy-mm-dd hh24:mi:ss') " + colNm);
			} else {
				valuesSB.append(", t."+ colNm);
			}
		}

	    trigSB.append(valuesSB.toString());
	    
	    trigSB.append("\nfrom backup, json_table(json, '$'\n columns (\n");
	    
	    for (Map<String, Object> rec : list) {
	    	colNm = rec.get("column_name").toString().toLowerCase();	
			valuesSB2.append(colNm + " path '$." + colNm + "', \n");		
	    }
	    
	    valuesSB2.deleteCharAt(valuesSB2.lastIndexOf(","));
	    
	    trigSB.append(valuesSB2.toString());
	    
	    trigSB.append("  )    \n ) as t  ");
	    
	    if(param.get("option").toString() != "") {
	    	trigSB.append("where " + param.get("option"));
	    }
	    
	    trigSB.append("\n ) \n select * from tbl where rn > " + param.get("startRow") + " and rn <= " + param.get("endRow") + " ");	    
	    
//	    logger.info(trigSB.toString());
	    
	    List<Map<String, Object>> resultList = jdbcTemplate.queryForList(trigSB.toString());
	   
	    return resultList;
	}

	@Override
	public List<CamelListMap> selectBackupColList(Map<String, Object> param) {		
		List<CamelListMap> cameList = new ArrayList<CamelListMap>();
		
		List<Map<String, Object>> colList = jdbcTemplate.queryForList("select column_name as field from user_tab_columns where table_name = '" + param.get("table").toString() + "'");		

		List<Map<String, Object>> backUplist = jdbcTemplate.queryForList("select column_name as field from user_tab_columns where table_name = 'IFBACKUP' AND column_name LIKE '%DATE%'");
		
		
		for(Map<String, Object> elem : colList) 
			cameList.add(CamelListMap.toCamelListMap(elem));
		
		for(Map<String, Object> elem : backUplist) 
			cameList.add(0, CamelListMap.toCamelListMap(elem));
			
		return cameList;
	}

	@Override
	public Map<String, Object> selectBackupCountSql(Map<String, Object> param) {
		StringBuffer trigSB = new StringBuffer();
	    StringBuffer valuesSB = new StringBuffer();
	    StringBuffer valuesSB2 = new StringBuffer();
	    String colNmStr = null;
	    String colNm = null;
	    String colType = null;
	    String nullable = null;
	    
	    trigSB.append("with backup as (\n");
	    trigSB.append("select to_char(backup_date, 'yyyymmdd') backup_date,tbl_nm,REPLACE(REPLACE(REPLACE(REPLACE(json, CHR(9), ''), CHR(10), ''), CHR(13), ''), '$, ', '') json from ifbackup where backup_date\n");
	    trigSB.append("between to_date('"+ param.get("dateFrom").toString()+"', 'yyyymmddhh24miss') and to_date('"+ param.get("dateTo").toString()+"', 'yyyymmddhh24miss') and tbl_nm='"+ param.get("table").toString().toLowerCase() +"')\n");
	    trigSB.append(",tbl AS (\n");
	    trigSB.append("select /*+ materialize */ rownum rn, backup_date ");	///*+ materialize */ 
	    
	    String sql = "select column_name,data_type,nullable from user_tab_columns where table_name = '"+ param.get("table").toString() + "'";
		
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		for (Map<String, Object> rec : list) {
			colNm = rec.get("column_name").toString().toLowerCase();
	    	colType = rec.get("data_type").toString().toLowerCase();
	    	nullable = rec.get("nullable").toString();	    
			
			if(colType.equals("varchar2") || colType.equals("nvarchar2") || colType.equals("char") || colType.equals("nchar") || colType.equals("clob")) {
				valuesSB.append(" , t."+colNm+" " +colNm);
			} else if(colType.equals("date")) {
				valuesSB.append(", to_char(to_date("+colNm+",'yyyymmddhh24miss'),'yyyy-mm-dd hh24:mi:ss') " + colNm);
			} else {
				valuesSB.append(", t."+ colNm);
			}
		}

	    trigSB.append(valuesSB.toString());
	    
	    trigSB.append("\nfrom backup, json_table(json, '$'\n columns (\n");
	    
	    for (Map<String, Object> rec : list) {
	    	colNm = rec.get("column_name").toString().toLowerCase();	
			valuesSB2.append(colNm + " path '$." + colNm + "', \n");		
	    }
	    
	    valuesSB2.deleteCharAt(valuesSB2.lastIndexOf(","));
	    
	    trigSB.append(valuesSB2.toString());
	    
	    trigSB.append("  )    \n ) as t\n");
	    
	    if(param.get("option").toString() != "") {
	    	trigSB.append("where " + param.get("option") + "\n ) \n select count(*) cnt from tbl where rn > " + param.get("startRow") + " and rn <= " + param.get("endRow") + " ");
	    }else {
	    	trigSB.append(") \n select count(*) cnt from tbl");
	    }	    
	    
	    Map<String, Object> result = jdbcTemplate.queryForMap(trigSB.toString(), null);
	    return result;
	}

	@Override
	public void doRecovery(Map<String, Object> param) throws IFException {		
		beginTransaction();
		
		try {
			recovery(param);
			deleteBackup(param);			
			endTransaction(true);
		} catch(Exception e) {
			endTransaction(false);
			throw e;
		}	    
	}
	
	private void recovery(Map<String, Object> param) throws IFException {
		StringBuffer trigSB = new StringBuffer();
	    StringBuffer valuesSB = new StringBuffer();
	    StringBuffer valuesSB2 = new StringBuffer();
	    StringBuffer valuesSB3 = new StringBuffer();
	    String colNmStr = null;
	    String colNm = null;
	    String colType = null;
	    String nullable = null;
	    String identity = null;
	    
	    String sql = "select column_name,data_type,nullable,identity_column from user_tab_columns where table_name = '"+ param.get("table").toString() + "'";	    
	    List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
	    
	    trigSB.append("insert into "+ param.get("table").toString() +"(\n");
	    
	    for (Map<String, Object> rec : list) {
			colNm = rec.get("column_name").toString().toLowerCase();
			identity = rec.get("identity_column").toString();
	    	if(identity.equals("NO")) 			
	    		valuesSB.append(colNm+", ");
		}
	    
	    valuesSB.deleteCharAt(valuesSB.lastIndexOf(","));
	    
	    trigSB.append(valuesSB.toString());
	    
	    trigSB.append(")\nwith backup as (\n");
	    trigSB.append("select to_char(backup_date, 'yyyymmdd') backup_date,tbl_nm,REPLACE(REPLACE(REPLACE(REPLACE(json, CHR(9), ''), CHR(10), ''), CHR(13), ''), '$, ', '') json from ifbackup where backup_date\n");
	    trigSB.append("between to_date('"+ param.get("dateFrom").toString()+"', 'yyyymmddhh24miss') and to_date('"+ param.get("dateTo").toString()+"', 'yyyymmddhh24miss') and tbl_nm='"+ param.get("table").toString().toLowerCase() +"')\n");
	    trigSB.append(",tbl AS (\n select /*+ materialize */");
	    

		for (Map<String, Object> rec : list) {
			colNm = rec.get("column_name").toString().toLowerCase();
	    	colType = rec.get("data_type").toString().toLowerCase();
	    	nullable = rec.get("nullable").toString();	    
	    	identity = rec.get("identity_column").toString();
	    	
	    	if(identity.equals("NO")) {
	    		if(colType.equals("varchar2") || colType.equals("nvarchar2") || colType.equals("char") || colType.equals("nchar") || colType.equals("clob")) {
					valuesSB2.append(colNm+", " );
				} else if(colType.equals("date")) {
					valuesSB2.append(" to_date("+colNm+",'yyyymmddhh24miss') "+ colNm + ", ");
				} else {
					valuesSB2.append(colNm+", ");
				}
	    	}			
		}
		
		valuesSB2.deleteCharAt(valuesSB2.lastIndexOf(","));

	    trigSB.append(valuesSB2.toString());
	    
	    trigSB.append("\nfrom backup, json_table(json, '$'\n columns (\n");
	    
	    for (Map<String, Object> rec : list) {
	    	colNm = rec.get("column_name").toString().toLowerCase();
	    	identity = rec.get("identity_column").toString();
	    	if(identity.equals("NO"))
	    		valuesSB3.append(colNm + " path '$." + colNm + "', \n");		
	    }
	    
	    valuesSB3.deleteCharAt(valuesSB3.lastIndexOf(","));
	    
	    trigSB.append(valuesSB3.toString());
	    
	    trigSB.append("  )    \n ) as t \n ) \n select * from tbl");	
	    
//	    logger.info(trigSB.toString());
	    
		batchUpdate(trigSB.toString(), new ArrayList<Object[]>());
	}

	private void deleteBackup(Map<String, Object> param) throws IFException {
		 StringBuffer trigSB = new StringBuffer();
		 
		 trigSB.append("delete from ifbackup where tbl_nm='"+ param.get("table").toString().toLowerCase() +"' AND backup_date\n");
		 trigSB.append("between to_date('"+ param.get("dateFrom").toString()+"', 'yyyymmddhh24miss') and to_date('"+ param.get("dateTo").toString()+"', 'yyyymmddhh24miss')");
		 		 
		 batchUpdate(trigSB.toString(), new ArrayList<Object[]>());
	}
	
	private void beginTransaction() throws IFException {
		try {
			conn = jdbcTemplate.getDataSource().getConnection();
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			try { conn.rollback(); } catch (SQLException e1) { }
			throw new IFException(ResponseStatus.FAIL, e.getMessage().replaceAll("\n", "").replaceAll("\"", "'"));
		}
	}
	
	private void endTransaction(boolean isSuccess) throws IFException {
		try {
			if(isSuccess)
				conn.commit();
			else
				conn.rollback();
			
			conn.close();
		} catch (SQLException e) {
			try { conn.rollback(); } catch (SQLException e1) { }
			throw new IFException(ResponseStatus.FAIL, e.getMessage().replaceAll("\n", "").replaceAll("\"", "'"));
		}
	}
	
	private void batchUpdate(String sql, List<Object[]> batchArgs) throws IFException {
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = conn.prepareStatement(sql);
			for(Object[] args : batchArgs) {
				for(int i=1; i <= args.length; i++)
					preparedStatement.setObject(i, args[i-1]);

		        preparedStatement.addBatch();
		    }
			
			preparedStatement.execute();
			
		} catch (SQLException e) {
			throw new IFException(ResponseStatus.FAIL, e.getMessage().replaceAll("\n", "").replaceAll("\"", "'"));
		} finally {
		    if(preparedStatement != null)
		        try { preparedStatement.close(); } catch (SQLException e) { }
		}
	}
	

}
