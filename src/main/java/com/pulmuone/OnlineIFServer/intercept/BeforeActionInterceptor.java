package com.pulmuone.OnlineIFServer.intercept;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.pulmuone.OnlineIFServer.common.ResponseStatus;
import com.pulmuone.OnlineIFServer.config.SystemConfig;
import com.pulmuone.OnlineIFServer.dto.UserInfo;
import com.pulmuone.OnlineIFServer.util.CUtil;
import com.pulmuone.OnlineIFServer.util.RestUtil;

@Component("beforeActionInterceptor")
public class BeforeActionInterceptor implements HandlerInterceptor {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());		

	@Autowired
	SystemConfig systemConfig;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		boolean isLogined = false;
		
		String authkey = request.getHeader("authkey");
		String interfaceId = request.getHeader("interfaceId");
		
		if(authkey != null && interfaceId != null) {
	    	Map sysMap = systemConfig.getSystemInfo(authkey);
	    	if(sysMap == null) {
	    		sendResponse(response);
				return false;
	    	}else
	    		request.setAttribute("isLogined", true);
			return HandlerInterceptor.super.preHandle(request, response, handler);
		}		
		
		if(!isLogined) {
			sendResponse(response);
			return false;
		}
		
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
	
	
	private void sendResponse(HttpServletResponse response) throws IOException {
    	Map<String, String> map = new HashMap<>();
		map.put("responseCode", "999");
    	map.put("responseMessage", "로그인이 필요합니다.");
    	
		response.setContentType("text/json; charset=UTF-8");
		response.getWriter().append(new Gson().toJson(map));
	}
}
