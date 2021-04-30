package com.pulmuone.OnlineIFServer.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pulmuone.OnlineIFServer.dao.CommonUIDao;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonUIServiceImpl implements CommonUIService {
	
	@Autowired
	CommonUIDao commonUIDao;

	@Override
	public List<Map> getSystems(Map<String, Object> param) {
		return commonUIDao.getSystems(param);
	}

	@Override
	public List<Map> getInterfaces(Map<String, Object> param) {
		return commonUIDao.getInterfaces(param);
	}

	@Override
	public int getLogCount(Map<String, Object> param) {
		return commonUIDao.getLogCount(param);
	}

	@Override
	public List<Map> getLogs(Map<String, Object> param) {
		return commonUIDao.getLogs(param);
	}

	@Override
	public Map getLog(Map<String, Object> param) {
		return commonUIDao.getLog(param);
	}

	@Override
	public List<Map> getDummies(Map<String, Object> param) {
		return commonUIDao.getDummies(param);
	}

	@Override
	public void insertDummy(Map<String, Object> param) {
		commonUIDao.insertDummy(param);
	}

	@Override
	public void updateDummy(Map<String, Object> param) {
		commonUIDao.updateDummy(param);
	}

	@Override
	public void deleteDummy(Map<String, Object> param) {
		commonUIDao.deleteDummy(param);
	}

	@Override
	public Map<String, String> getDate() {
		return commonUIDao.getDate();
	}

	@Override
	public List<Map<String, String>> getSuccessCount(Map<String, String> list) {
		return commonUIDao.getSuccessCount(list);
	}

	@Override
	public List<Map<String, String>> getFailCount(Map<String, String> list) {
		return commonUIDao.getFailCount(list);
	}
}
