package com.pulmuone.OnlineIFServer.service;

import java.util.Map;

import com.pulmuone.OnlineIFServer.dto.UserInfo;

public interface JwtService {
	String create(String key, UserInfo data);

	boolean isUsable(String token) throws Exception;

	Map<String, Object> get(String key) throws Exception;
}
