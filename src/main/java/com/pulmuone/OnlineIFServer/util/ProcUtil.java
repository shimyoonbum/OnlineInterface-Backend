package com.pulmuone.OnlineIFServer.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.pulmuone.OnlineIFServer.common.IfLogger;
import com.pulmuone.OnlineIFServer.common.ResponseStatus;
import com.pulmuone.OnlineIFServer.config.InterfaceConfig;
import com.pulmuone.OnlineIFServer.config.MetaConfig;
import com.pulmuone.OnlineIFServer.config.SystemConfig;
import com.pulmuone.OnlineIFServer.service.CommonService;
import com.pulmuone.OnlineIFServer.service.ProcedureService;
import com.google.gson.Gson;

@Component
public class ProcUtil {
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

	@Autowired
	ProcedureService procedureService;

	public void interfaces(HttpServletRequest request, HttpServletResponse response) {
		interfaces(request, response, null);
	}

	public void interfaces(HttpServletRequest request, HttpServletResponse response, Map<String,Object> resourceMap) {
		Map<String, Object> reqMap = getRequest(request, response, resourceMap);
		if(reqMap == null)
			return;
		
    	String rtn = procedureService.interfaceFunction(reqMap);
    	String rtnCode = rtn.substring(0,3);
    	String json = rtn.substring(4);	//json.length();
        //if (json == null) {
        //	sendResponse(reqMap, json, "N", ResponseStatus.FAIL, HttpStatus.NOT_FOUND, request, response);
        //	return;
        //}
    	//Map<String, Object> map = new Gson().fromJson(json, Map<String, Object>);
    	sendResponse(reqMap, json, "N", ResponseStatus.getStatusByValue(rtnCode), HttpStatus.OK, request, response);
	}
	
	public Map<String, Object> getRequest(HttpServletRequest request, HttpServletResponse response, Map<String,Object> resourceMap) {
		Map<String, Object> reqMap = new HashMap<String, Object>();
		if(!verifyRequest(request, response, reqMap, resourceMap))
			return null;
		
		ByteArrayOutputStream bos= new ByteArrayOutputStream();
		
		String bstr = null;
		int numRead;
	    byte b[] = new byte[1024];
		ServletInputStream sis;
		try {
			sis = request.getInputStream();
			while((numRead = sis.read(b,0,1024)) != -1){
		    	bos.write(b,0,numRead);
		    }
			bstr = bos.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(bos != null)
					bos.close();
			} catch (IOException e) {}
		}
		reqMap.put("param_string",  bstr==null || bstr.equals("")? "{}":bstr);
		
		return reqMap;
	}
	
	public boolean verifyRequest(HttpServletRequest request, HttpServletResponse response, Map reqMap, Map<String,Object> resourceMap) {
		String interfaceId = request.getHeader("interfaceId");
		String authkey = request.getHeader("authkey");
		
	    Map systemInfo = systemConfig.getSystemInfo(authkey);
    	String systemId1 = systemInfo.get("systemId").toString();
    	Map interfaceInfo = interfaceConfig.getInterfaceInfo(systemId1+"_"+interfaceId);
    	String systemId2 = interfaceInfo==null? "UNKNOWN":interfaceInfo.get("systemId").toString();
//    	String procNm = (String)interfaceInfo.get("procNm");
    	
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("interfaceId", interfaceId);
    	//TODO; map.put("authkey", authkey);
//    	map.put("procNm", procNm);
    	map.put("systemId", systemId1);
    	
    	if(resourceMap != null)
	        for (String key : resourceMap.keySet())
	        	map.put(key, resourceMap.get(key));
    	
    	map.put("uri", request.getRequestURI());
    	map.put("client", request.getRemoteHost());
    	String queryString = null;
		try {
			queryString = request.getQueryString()==null? null : URLDecoder.decode(request.getQueryString(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			sendResponse(reqMap, null, "N", ResponseStatus.FAIL, HttpStatus.NOT_FOUND, request, response);
        	return false;
		}
    	if(queryString != null) {
    		String[] queryParams = queryString.split("&");
    		for (String queryParam : queryParams) {
    			String[] queries = queryParam.split("=");
    			map.put(queries[0], queries[1]);
    		}
    	}
    	
    	reqMap.put("common_map", map);
    	reqMap.put("common_string", CUtil.convertMapToJsonString(map));
    	
    	if(interfaceInfo==null || systemInfo==null || !systemId1.equals(systemId2)) {
    		sendResponse(reqMap, null, "N", ResponseStatus.UNKNOWN, HttpStatus.NOT_FOUND, request, response);
        	return false;
    	}
    	
    	String dummyResponse = commonService.findDummy(map);
    	if(dummyResponse != null) {
        	Map resMap = new Gson().fromJson(dummyResponse, Map.class);
    		sendResponse(reqMap, dummyResponse, "Y", ResponseStatus.getStatusByValue((String)resMap.get("responseCode")), HttpStatus.OK, request, response);
        	return false;
    	}
    	
    	/**
    	String mandatory = (String)interfaceInfo.get("mandatory");
    	if(reqMap == null || mandatory == null || mandatory.trim().isEmpty())
    		return true;
    	
    	String[] mandatories = mandatory.split(",");
    	for (String elem : mandatories) {
    		if(reqMap.get(elem) != null)
    			continue;
    		
    		sendResponse(reqMap, null, "N", ResponseStatus.MISSING, HttpStatus.NOT_FOUND, request, response);
        	return false;
    	}
    	**/
    	
        return true;
	}
	
	public void sendResponse(Map reqMap, String resStr, String isDummy, ResponseStatus respStatus, HttpStatus httpStatus, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/json;charset=\"UTF-8\"");
		if(resStr==null) {
	    	Map<String, Object> map = new HashMap<String, Object>();
	    	map.put("responseCode", respStatus.value());
	    	map.put("responseMessage", respStatus.phrase());
	    	resStr= CUtil.convertMapToJsonString(map);
		}

		try {
			ServletOutputStream sos = response.getOutputStream();
	    	sos.write(resStr.getBytes());
	    	sos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Map<String,Object> commonMap = (Map<String,Object>) reqMap.get("common_map");
		String ifId = commonMap.get("interfaceId").toString();
		String systemId = commonMap.get("systemId").toString();
		
    	ifLogger.log(request, ifId, isDummy, respStatus.value(), systemId, reqMap, resStr);
	}
	
}
