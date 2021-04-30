package com.pulmuone.OnlineIFServer.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pulmuone.OnlineIFServer.config.SystemConfig;
import com.pulmuone.OnlineIFServer.service.CommonService;
import com.google.gson.Gson;

@Component
public class IfLogger {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
	@Autowired
	CommonService commonService;		

	@Autowired
	SystemConfig systemConfig;

	synchronized public void log(HttpServletRequest request, String ifId, String isDummy, String ifResult, String systemId, Map<String, Object> reqMap, Map<String, Object> resMap) {
		log(request, ifId, isDummy, ifResult, systemId, reqMap, new Gson().toJson(resMap));
	}

	synchronized public void log(HttpServletRequest request, String ifId, String isDummy, String ifResult, String systemId, Map<String, Object> reqMap, String resJson) {
    	Gson gson = new Gson();
		
    	Map<String, Object> param = new HashMap();
    	param.put("ifId", ifId);
    	param.put("ifResult", ifResult);
    	param.put("isDummy", isDummy);
    	param.put("systemId", systemId);
    	param.put("method", request.getMethod());
    	param.put("uri", request.getRequestURI());
    	param.put("serverId", System.getProperty("server.id"));
    	try {
			param.put("param", request.getQueryString()==null? null : URLDecoder.decode(request.getQueryString(), "utf-8"));
		} catch (UnsupportedEncodingException e) {}
    	param.put("client", request.getRemoteHost());
    	reqMap.remove("param_string");
    	reqMap.remove("common_map");
    	reqMap.remove("common_string");  	
    	//1-27 sim 로그 남길 때만 특수문자 escape 처리
    	param.put("reqJson", reqMap==null? "": gson.toJson(reqMap).replaceAll("\\\\r|\\\\n|\\\\t|\\\\", ""));
    	param.put("resJson", resJson);
    	param.put("time", systemConfig.getTime());

    	commonService.log(param);
	}
}
