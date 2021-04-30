package com.pulmuone.OnlineIFServer.service;

import java.util.List;
import java.util.Map;

import com.pulmuone.OnlineIFServer.dto.UserInfo;

public interface LoginService {
	
	public int doLogin(Map<String, Object> param);

	public int checkPw(Map<String, Object> param);

	public void updatePassword(Map<String, Object> param);

	public void register(Map<String, Object> param);

	public UserInfo checkEmpty(Map<String, Object> param);
	
}
