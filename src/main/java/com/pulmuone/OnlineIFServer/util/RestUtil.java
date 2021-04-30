package com.pulmuone.OnlineIFServer.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.pulmuone.OnlineIFServer.common.IfLogger;
import com.pulmuone.OnlineIFServer.common.ResponseStatus;
import com.pulmuone.OnlineIFServer.config.InterfaceConfig;
import com.pulmuone.OnlineIFServer.config.MetaConfig;
import com.pulmuone.OnlineIFServer.config.SystemConfig;
import com.pulmuone.OnlineIFServer.service.CommonService;
import com.google.gson.Gson;

@Component
public class RestUtil {
	@Autowired
	IfLogger ifLogger;

	@Autowired
	SystemConfig systemConfig;

	@Autowired
	InterfaceConfig interfaceConfig;

	@Autowired
	MetaConfig metaConfig;

	@Autowired
	CommonService commonService;

	public ResponseEntity<?> verifyRequest(HttpServletRequest request, Map reqMap) {
		String interfaceId = request.getHeader("interfaceId");
		String authkey = request.getHeader("authkey");
		
	    Map systemInfo = systemConfig.getSystemInfo(authkey);
    	String systemId1 = systemInfo.get("systemId").toString();
    	Map interfaceInfo = interfaceConfig.getInterfaceInfo(systemId1+"_"+interfaceId);
    	String systemId2 = interfaceInfo==null? "UNKNOWN":interfaceInfo.get("systemId").toString();
    	
    	//Map interfaceInfo = interfaceConfig.getInterfaceInfo(request.getMethod()+request.getRequestURI());
    	if(interfaceInfo==null || systemInfo==null || !systemId1.equals(systemId2))
        	return getResponse(reqMap, new HashMap(), request, ResponseStatus.UNKNOWN, HttpStatus.NOT_FOUND);
    	
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("interfaceId", interfaceId);
    	map.put("systemId", systemId1);
    	String dummyResponse = commonService.findDummy(map);
    	if(dummyResponse != null) {
        	Map resMap = new Gson().fromJson(dummyResponse, Map.class);
        	return getEntity(reqMap, resMap, request, "Y", (String)resMap.get("responseCode"), HttpStatus.OK, interfaceInfo);
    	}
    	
    	/**
    	String mandatory = (String)interfaceInfo.get("mandatory");
    	if(reqMap == null || mandatory == null || mandatory.trim().isEmpty())
    		return null;
    	
    	String[] mandatories = mandatory.split(",");
    	for (String elem : mandatories) {
    		if(reqMap.get(elem) != null)
    			continue;
    		
        	return getResponse(reqMap, new HashMap(), request, ResponseStatus.MISSING, HttpStatus.NOT_FOUND);
    	}
    	**/
    	
        return null;
	}
	
	public ResponseEntity<?> getResponse(Map reqMap, Map resMap, HttpServletRequest request, ResponseStatus respStatus, HttpStatus httpStatus) {
		resMap.put("responseCode",respStatus.value());
    	resMap.put("responseMessage",respStatus.phrase());
    	
    	Map map = (Map) reqMap.get("common_map");
    	String systemId = null;	//TODO; systemInfo.get("systemId").toString();
		String interfaceId = request.getHeader("interfaceId");
    	Map interfaceInfo = interfaceConfig.getInterfaceInfo(systemId+"_"+interfaceId);
    	//Map interfaceInfo = interfaceConfig.getInterfaceInfo(request.getMethod()+request.getRequestURI());
    	return getEntity(reqMap, resMap, request, "N", respStatus.value(), httpStatus, interfaceInfo);
	}

	private ResponseEntity<?> getEntity(Map reqMap, Map resMap, HttpServletRequest request, String isDummy, String ifResult, HttpStatus httpStatus, Map interfaceInfo) {
		String ifId = interfaceInfo==null? "UNKNOWN":interfaceInfo.get("ifId").toString();
    	String systemId = interfaceInfo==null? "UNKNOWN":interfaceInfo.get("systemId").toString();
    	
    	ifLogger.log(request, ifId, isDummy, ifResult, systemId, reqMap, resMap);
    	
        return new ResponseEntity<Map>(resMap, httpStatus);
	}
}
