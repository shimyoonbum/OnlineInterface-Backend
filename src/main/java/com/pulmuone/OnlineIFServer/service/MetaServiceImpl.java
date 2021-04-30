package com.pulmuone.OnlineIFServer.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pulmuone.OnlineIFServer.common.Constants;
import com.pulmuone.OnlineIFServer.common.IFException;
import com.pulmuone.OnlineIFServer.config.MetaConfig;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MetaServiceImpl implements MetaService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	MetaConfig metaConfig;
	
	@Autowired
	MetaServiceInsert serviceInsert;

	@Autowired
	MetaServiceSelect serviceSelect;

	@Autowired
	MetaServiceUpdate serviceUpdate;

	//@Autowired
	//MetaServiceTerminate serviceTerminate;
	
	public String[] interfaceMeta(Map<String, Object> reqMap) {
		Map<String, Object> commonMap = (Map<String, Object>) reqMap.get("common_map");
		Map<String, Object> paramMap = (Map<String, Object>) reqMap.get("param_map");
		String interfaceId = commonMap.get("interfaceId").toString();
		
    	Map metaMap= null;
		try {
			metaMap = metaConfig.getMetaInfo(interfaceId);			
		} catch (Exception e) {
    		return new String[] {"999", "{\"responseCode\":\"999\", \"responseMessage\":\""+e.getMessage()+"\"}"};    		
		}
    	if(metaMap == null)
    		return new String[] {"999", "{\"responseCode\":\"999\", \"responseMessage\":\"META 정의가 없습니다.\"}"};
    	
    	List<Map> children = (List<Map>) metaMap.get("children");
		if(children == null)
    		return new String[] {"999", "{\"responseCode\":\"999\", \"responseMessage\":\"META attribute 정의가 없습니다.\"}"};
		
		try {
			for(Map child : children) {
				String action = child.get(Constants.action).toString();
				if(action.equals("C"))
					return serviceInsert.insertChildMap(child, commonMap, paramMap);
				else if(action.equals("R"))
					return serviceSelect.selectChildMap(metaMap, child, commonMap, paramMap);
				else if(action.equals("U"))
					return serviceUpdate.updateChildMap(metaMap, child, commonMap, paramMap);
				//else if(action.equals("X"))
				//	return serviceTerminate.terminateChildMap(child, commonMap, paramMap);
			}
		} catch (IFException e) {
    		return new String[] {e.getErrorCode(), "{\"responseCode\":\""+e.getErrorCode()+"\", \"responseMessage\":\""+e.getErrorMessage()+"\"}"};
		} catch (Exception e) {
    		return new String[] {"999", "{\"responseCode\":\"999\", \"responseMessage\":\""+e.getMessage()+"\"}"};
		}
		
		return new String[] {"000", ""};
	}

}
