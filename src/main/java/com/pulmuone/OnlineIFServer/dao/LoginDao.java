package com.pulmuone.OnlineIFServer.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.pulmuone.OnlineIFServer.dto.UserInfo;

@Mapper // 이렇게 해주면, UserDao의 구현체를 마이바티스가 대신 구현해준다.
public interface LoginDao {
	public int doLogin(Map<String, Object> param);

	public int checkPw(Map<String, Object> param);

	public void updatePassword(Map<String, Object> param);

	public void register(Map<String, Object> param);

	public UserInfo checkEmpty(Map<String, Object> param);
}
