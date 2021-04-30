package com.pulmuone.OnlineIFServer.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pulmuone.OnlineIFServer.dao.CommonDao;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonServiceImpl implements CommonService {
	
	@Autowired
	CommonDao commonDao;

	//configuration
	@Override
	public List<Map> systems() {
		return commonDao.systems();
	}

	@Override
	public List<Map> interfaces(Map<String, Object> param) {
		return commonDao.interfaces(param);
	}

	@Override
	public List<Map> metas(Map<String, Object> param) {
		return commonDao.metas(param);
	}

	@Override
	public List<Map> metaAttrs(Map<String, Object> param) {
		return commonDao.metaAttrs(param);
	}

	@Override
	public void log(Map<String, Object> param) {
		if(param.get("param")==null)
			param.put("param", "");
		commonDao.log(param);
	}

	@Override
	public String findDummy(Map<String, Object> param) {
		return commonDao.findDummy(param);
	}

}
