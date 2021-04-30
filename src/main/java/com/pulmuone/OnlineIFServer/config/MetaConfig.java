package com.pulmuone.OnlineIFServer.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pulmuone.OnlineIFServer.common.Constants;
import com.pulmuone.OnlineIFServer.service.CommonService;
import com.pulmuone.OnlineIFServer.util.CUtil;
 
@Component
public class MetaConfig {
	@Autowired
	CommonService commonService;

	public final static String ifCols = "ifCols";
	public final static String ifDBCols = "if_cols";
	public final static String childrenMap = "children";
	
    private static Map<String,Object> metaConfigMap = null;
    
    synchronized public Map getMetaInfo(String key) throws Exception {
    	if(metaConfigMap == null)
    		load("all");
    	
    	return (Map) metaConfigMap.get(key);
    }
    
    synchronized public String load(String ifId) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		Map<String,Object> metaMap = null;
		
    	if(MetaConfig.metaConfigMap == null)
    		ifId = "all";
    	
    	if(ifId.equals("all")) {
    		metaMap = new HashMap<String,Object>();
    	} else {
    		param.put(Constants.ifId, ifId);
    		metaMap = MetaConfig.metaConfigMap;
    	}
    	
		List<Map> metas = setMetas(param, metaMap);
		
        linkMetaAndAttrs(param, metaMap);
    	
    	String verifyMsg = verifyMetas(metaMap, metas);
    	
    	for(String key : metaMap.keySet()) {
    		if(!ifId.equals("all") && !key.equals(ifId))
    			continue;
    		
    		List<Map> list = null;
    		try {
    			list = (List<Map>) metaMap.get(key);
        		
        		setMetaChild(metaMap, list, key);
    		} catch(Exception e) {
    			System.out.println("MetaConfig.load:"+ e.getMessage());
    		}
    	}
    	
    	if(ifId.equals("all"))
    		MetaConfig.metaConfigMap = metaMap;
   	
    	return verifyMsg;
	}

	private List<Map> setMetas(Map<String, Object> param, Map<String,Object> metaMap) {
		String ifId = null;
    	List<Map> list = null;
    	
    	//metaMap = new HashMap<String,Object>();
    	List<Map> metas = commonService.metas(param);
    	for(Map meta : metas) {
    		if(ifId!=null && ifId.equals(meta.get(Constants.ifId).toString())) {
        		list.add(meta);
        		continue;
    		}
    		
    		if(list != null && list.size() > 0)
    			metaMap.put(ifId, list);

    		ifId= meta.get(Constants.ifId).toString();
			list = new ArrayList<Map>();
			
    		list.add(meta);
    	}
		if(list != null && list.size() > 0)
    		metaMap.put(ifId, list);
		
		///System.out.println("MetaConfig>getMetaMap>metaMap:"+CUtil.convertMapToJsonString(metaMap));
		return metas;
	}

	private void linkMetaAndAttrs(Map<String, Object> param, Map<String,Object> metaMap) throws Exception {
		String ifId = null;
    	String ifTbl = null;
    	List<Map> list = null;
    	List<Map> metaAttrs = commonService.metaAttrs(param);
    	///System.out.println("MetaConfig>getMetaMap>metaAttrs:"+CUtil.convertListToJsonString(metaAttrs));
    	
    	for(Map metaAttr : metaAttrs) {
    		String cTbl = metaAttr.get(Constants.ifTbl).toString();
    		String cId = metaAttr.get(Constants.ifId).toString();
    		if(ifId!=null && ifTbl!=null && ifId.equals(cId) && ifTbl.equals(cTbl)) {
        		list.add(metaAttr);
        		continue;
    		}
    		
			setMetaAttr(metaMap, ifId, ifTbl, list);

			ifId= cId;
			ifTbl= cTbl;
			list = new ArrayList<Map>();
			
    		list.add(metaAttr);
    	}
    	setMetaAttr(metaMap, ifId, ifTbl, list);
	}

	private String verifyMetas(Map<String,Object> metaMap, List<Map> metas) {
    	StringBuffer sb = new StringBuffer();
		String ifId;
		String ifTbl;
		for(Map meta : metas) {
    		ifId = meta.get(Constants.ifId).toString();
    		ifTbl = meta.get(Constants.ifTbl).toString();
    		
        	for(Map map : (List<Map>) metaMap.get(ifId)) {
        		if(!ifTbl.equals(map.get(Constants.ifTbl)))
        			continue;
        		
        		if(map.get(ifCols)==null)
        			sb.append(ifId+"-"+ifTbl+", ");
        		
        		break;
        	}
    	}
		
		return sb.toString();
	}

	private void setMetaChild(Map<String,Object> metaMap, List<Map> list, String key) {
		Map top = new HashMap();
		metaMap.put(key, top);
		List<Map> children = null;
		
		for(Map meta : list) {
			if(meta.get(Constants.parent)==null) {
				children = (List<Map>) top.get(childrenMap);
				if(children == null) {
					children = new ArrayList<Map>();
					top.put(childrenMap, children);
				}
				children.add(meta);
				continue;
			}
			
			Map parent = findParentMeta(meta.get(Constants.parent).toString(), list);
			if (parent == null)
				continue;
			
			children = (List<Map>) parent.get(childrenMap);
			if(children == null) {
				children = new ArrayList<Map>();
				parent.put(childrenMap, children);
			}
			children.add(meta);
		}
	}
    
    private Map findParentMeta(String parent, List<Map> list) {
		if(list == null || list.size() == 0)
			return null;
		
    	for(Map meta : list) {
    		if(parent.equals(meta.get(Constants.path).toString()))
    			return meta;
    	}
    	return null;
    }
    
    private void setMetaAttr(Map<String,Object> metaMap, String ifId, String ifTbl, List<Map> list) throws Exception {
		if(ifId == null || ifTbl == null || metaMap == null || list == null || list.size() == 0)
			return;
		
		if(metaMap.get(ifId)==null)
			throw new Exception(ifId+" META 정의가 없습니다.");
		
    	for(Map map : (List<Map>) metaMap.get(ifId)) {
    		if(!ifTbl.equals(map.get(Constants.ifTbl)))
    			continue;
    		
    		map.put(ifDBCols, list);
    		return;
    	}
    }
}

/** metaConfig **
{
	IF_PO_INP={
		children=[{
			ifId=IF_PO_INP, 
			ifTbl=tb_po_info, 
			path=header,
			alias=i, 
			action=C
			ifCols=[
				{ifCol=hrdSeq, ifTbl=tb_po_info, ifId=IF_PO_INP, useYn=Y, dbCol=hrd_seq}, 
				{ifCol=ordDate, ifTbl=tb_po_info, ifId=IF_PO_INP, useYn=Y, dbCol=ord_date, conv=to_char(to_date($,'yyyy-mm-dd hh24:mi:ss'),'yyyymmdd')}, 
				{ifCol=srcSvr, ifTbl=tb_po_info, ifId=IF_PO_INP, useYn=Y, dbCol=src_svr}
			],
			children=[{
				ifId=IF_PO_INP, 
				ifTbl=tb_po_dtl, 
				path=line,
				action=C,
				parent=header, 
				alias=d, 
				action=C,
				ifCols=[
					{ifCol=erpItmNo, ifTbl=tb_po_dtl, ifId=IF_PO_INP, useYn=Y, dbCol=erp_itm_no}, 
					{parentCol=hrd_seq, ifCol=hrdSeq, ifTbl=tb_po_dtl, ifId=IF_PO_INP, useYn=Y, dbCol=hrd_seq}
				]
			}]
		}]
	}
}
**/
