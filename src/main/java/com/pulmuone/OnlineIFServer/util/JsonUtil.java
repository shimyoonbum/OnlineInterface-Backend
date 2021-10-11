package com.pulmuone.OnlineIFServer.util;

import javax.servlet.http.HttpServletRequest;

public final class JsonUtil {
	
	public static boolean isJsonAccept(HttpServletRequest request) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.toLowerCase().startsWith("application/json")) {
			return true;
		}
		return false;
	}
	
	public static boolean isJsonBody(HttpServletRequest request) {
		String contentType = request.getContentType();
		if (contentType != null && contentType.toLowerCase().startsWith("application/json") ){
			return true;
		}
		return false;
	}

	public static boolean isJsonErrorAccept(HttpServletRequest request) {
		if ("application/json".equals(request.getHeader("X-Error-Accept"))) {
			return true;
		}
		return false;
	}
	
	public static boolean isJson(HttpServletRequest request) {
		if (isJsonBody(request)) {
			return true;
		}
		return false;
	}

}

