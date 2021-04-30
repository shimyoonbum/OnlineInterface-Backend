package com.pulmuone.OnlineIFServer.intercept;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.pulmuone.OnlineIFServer.common.ResponseStatus;
import com.google.gson.Gson;

// 인터셉터 이름
@Component("needToLoginInterceptor")
public class NeedToLoginInterceptor implements HandlerInterceptor {
	
	@SuppressWarnings("unused")
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 이 인터셉터 실행 전에 beforeActionInterceptor 가 실행되고 거기서 isLogined 라는 속성 생성
		// 그래서 여기서 단순히 request.getAttribute("isLogined"); 이것만으로 로그인 여부 알 수 있음
		boolean isLogined = (boolean) request.getAttribute("isLogined");

		if (isLogined)
			return HandlerInterceptor.super.preHandle(request, response, handler);
		
 		String interfaceId = request.getHeader("interfaceId");
		String authkey = request.getHeader("authkey");
		if (interfaceId != null && authkey != null) {
	    	Map<String, Object> map = new HashMap<>();
			if (interfaceId == null) {
				map.put("responseCode", ResponseStatus.IF_ID.value());
		    	map.put("responseMessage", ResponseStatus.IF_ID.phrase());
			}
			else if (authkey == null) {
				map.put("responseCode", ResponseStatus.AUTHKEY.value());
		    	map.put("responseMessage", ResponseStatus.AUTHKEY.phrase());
			}
	    	
			response.setContentType("text/json; charset=UTF-8");
			response.getWriter().append(new Gson().toJson(map));
			// 리턴 false;를 이후에 실행될 인터셉터와 액션이 실행되지 않음
			return false;
		}

		response.setContentType("text/html; charset=UTF-8");
		response.getWriter().append("<script>");
		response.getWriter().append("alert('로그인 후 이용해주세요.');");
		response.getWriter().append("location.replace('/');");
		response.getWriter().append("</script>");
		// 리턴 false;를 이후에 실행될 인터셉터와 액션이 실행되지 않음
		return false;
	}
}
