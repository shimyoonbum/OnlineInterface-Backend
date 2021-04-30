package com.pulmuone.OnlineIFServer.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper // 이렇게 해주면, UserDao의 구현체를 마이바티스가 대신 구현해준다.
public interface UserDao {
	public List<Map> getUser();

	public Map findById(long id);

	public void saveUser(Map<String, Object> param);

	public void updateUser(Map<String, Object> param);

	public void deleteUserById(long id);
}
