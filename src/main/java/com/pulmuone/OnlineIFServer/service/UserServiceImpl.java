package com.pulmuone.OnlineIFServer.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pulmuone.OnlineIFServer.dao.UserDao;
import com.pulmuone.OnlineIFServer.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
	@Autowired
	UserDao userDao;

	@Override
	public List<Map> getUser() {
		return userDao.getUser();
	}

	@Override
	public void saveUser(Map<String, Object> param) {
		userDao.saveUser(param);

		return;
	}

	@Override
	public Map findById(long id) {
		return userDao.findById(id);
	}

	@Override
	public void deleteUserById(long id) {
		userDao.deleteUserById(id);
	}

	@Override
	public void updateUser(Map<String, Object> param) {
		userDao.updateUser(param);
	}
}
