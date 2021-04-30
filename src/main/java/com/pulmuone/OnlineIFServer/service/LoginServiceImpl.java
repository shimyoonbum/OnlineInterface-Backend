package com.pulmuone.OnlineIFServer.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pulmuone.OnlineIFServer.dao.LoginDao;
import com.pulmuone.OnlineIFServer.dao.UserDao;
import com.pulmuone.OnlineIFServer.dto.UserInfo;
import com.pulmuone.OnlineIFServer.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {
	@Autowired
	LoginDao loginDao;

	@Override
	public int doLogin(Map<String, Object> param) {
		return loginDao.doLogin(param);
	}

	@Override
	public int checkPw(Map<String, Object> param) {
		// TODO Auto-generated method stub
		return loginDao.checkPw(param);
	}

	@Override
	public void updatePassword(Map<String, Object> param) {
		// TODO Auto-generated method stub
		loginDao.updatePassword(param);
	}

	@Override
	public void register(Map<String, Object> param) {
		loginDao.register(param);		
	}

	@Override
	public UserInfo checkEmpty(Map<String, Object> param) {
		// TODO Auto-generated method stub
		return loginDao.checkEmpty(param);		
	}
}
