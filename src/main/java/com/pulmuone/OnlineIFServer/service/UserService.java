package com.pulmuone.OnlineIFServer.service;

import java.util.List;
import java.util.Map;

public interface UserService {
	public List<Map> getUser();

	public Map findById(long id);

	public void saveUser(Map<String, Object> param);

	public void updateUser(Map<String, Object> param);

	public void deleteUserById(long id);
}
