package com.pulmuone.OnlineIFServer.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.pulmuone.OnlineIFServer.common.IfLogger;
import com.pulmuone.OnlineIFServer.common.ResponseStatus;
import com.pulmuone.OnlineIFServer.config.InterfaceConfig;
import com.pulmuone.OnlineIFServer.config.MailConfig;
import com.pulmuone.OnlineIFServer.config.SystemConfig;
import com.pulmuone.OnlineIFServer.service.CommonService;
import com.pulmuone.OnlineIFServer.service.MetaService;
import com.google.gson.Gson;
import com.google.gson.stream.MalformedJsonException;

@Component
public class MetaUtil {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
	@Autowired
	IfLogger ifLogger;
	
	@Autowired
	SystemConfig systemConfig;

	@Autowired
	InterfaceConfig interfaceConfig;

	@Autowired
	CommonService commonService;

	@Autowired
	MetaService metaService;
	
	@Autowired
	MailConfig mail;

	public void interfaces(HttpServletRequest request, HttpServletResponse response) {
		systemConfig.setTime();
		interfaces(request, response, null);
	}

	public void interfaces(HttpServletRequest request, HttpServletResponse response, Map<String,Object> resourceMap) {
		Map<String, Object> reqMap = getRequest(request, response, resourceMap);
		if(reqMap == null)
			return;
		
    	String[] rtn = metaService.interfaceMeta(reqMap);
    	String rtnCode = rtn[0];
    	String json = rtn[1];
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
		try {
			Map jsonMap = new Gson().fromJson(reqMap.get("param_string").toString(), Map.class);
	    	reqMap.put("param_map", jsonMap);
		} catch (Exception e) {
			sendResponse(reqMap, null, "N", ResponseStatus.JSON, HttpStatus.NOT_FOUND, request, response);
        	return null;
		}
		
		return reqMap;
	}
	
	public boolean verifyRequest(HttpServletRequest request, HttpServletResponse response, Map reqMap, Map<String,Object> resourceMap) {
		String interfaceId = request.getHeader("interfaceId");
		String authkey = request.getHeader("authkey");
		
	    Map systemInfo = systemConfig.getSystemInfo(authkey);
    	String systemId1 = systemInfo.get("systemId").toString();
    	Map interfaceInfo = interfaceConfig.getInterfaceInfo(systemId1+"_"+interfaceId);
    	if(interfaceInfo==null) {
    		sendResponse(reqMap, null, "N", ResponseStatus.UNKNOWN, HttpStatus.NOT_FOUND, request, response);
        	return false;
    	}
    	
    	String systemId2 = interfaceInfo==null? "UNKNOWN":interfaceInfo.get("systemId").toString();
    	String procNm = (String)interfaceInfo.get("procNm");
    	
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("interfaceId", interfaceId);
    	map.put("procNm", procNm);
    	map.put("systemId", systemId1);
    	
    	if(resourceMap != null)
	        for (String key : resourceMap.keySet())
	        	map.put(key, resourceMap.get(key));
    	
    	map.put("uri", request.getRequestURI());
    	map.put("client", request.getRemoteHost());
    	String queryString= null;
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
    			if(queries.length > 1)
    				map.put(queries[0], queries[1]);
    		}
    	}
    	
    	reqMap.put("common_map", map);
    	reqMap.put("common_string", CUtil.convertMapToJsonString(map));
    	
    	if(systemInfo==null || !systemId1.equals(systemId2)) {
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
	
	//reqMap : systemId, client, IFid, page, uri, target 정보, resStr : 응답되는 json Map
	public void sendResponse(Map reqMap, String resStr, String isDummy, ResponseStatus respStatus, HttpStatus httpStatus, HttpServletRequest request, HttpServletResponse response) {
		Gson gson = new Gson();	
		
		response.setContentType("text/json;charset=\"UTF-8\"");	
		
		if(resStr==null || resStr.equals("")) {
	    	Map<String, Object> map = new HashMap<String, Object>();
	    	map.put("responseCode", respStatus.value());
	    	map.put("responseMessage", respStatus.phrase());
	    	resStr= CUtil.convertMapToJsonString(map);
		}		
		try {
			ServletOutputStream sos = response.getOutputStream();
	    	sos.write(resStr.getBytes());
	    	sos.flush();	    	
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		Map<String,Object> commonMap = (Map<String,Object>) reqMap.get("common_map");
		if(commonMap==null || commonMap.get("interfaceId")==null)
			return;		
		
		String ifId = commonMap.get("interfaceId").toString();
		String systemId = commonMap.get("systemId").toString();
		
		//인터페이스 항목명 가져오기
		Map interfaceInfo = interfaceConfig.getInterfaceInfo(systemId+"_"+ifId);
		
		//2021-01-14 SIM 실패 메세지 Split
    	String[] failReason = gson.fromJson(resStr, new HashMap().getClass()).get("responseMessage").toString().split(" | ", 5);
    	
    	//2021-03-09 SIM method, ifname, requestData 관련 로직 추가
    	String method = interfaceInfo.get("method").toString();
    	String name = interfaceInfo.get("ifNm").toString();
    	String requestData = reqMap.get("param_string").toString();
    	
    	if(method.equalsIgnoreCase("get"))
    		requestData = null;
    	
    	try {
    		if(respStatus.value().equals("999")) {
        		if(failReason.length == 1)
        			mail.sendFailMail(ifId, systemId, name, requestData, "NullPointer Exception");
        		else
        			mail.sendFailMail(ifId, systemId, name, requestData, failReason[4]);
    	    }
        	
    	}catch(Exception e) {
//    		logger.info("메세징 오류 발생!! \n" + e.getMessage());
    	}    	
		
    	ifLogger.log(request, ifId, isDummy, respStatus.value(), systemId, reqMap, resStr);
	}
	
}
