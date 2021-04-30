package com.pulmuone.OnlineIFServer.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pulmuone.OnlineIFServer.dao.CommonDao;
import com.pulmuone.OnlineIFServer.dao.MailDao;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MailServiceImpl implements MailService {
	
	@Autowired
	MailDao mailDao;

	@Override
	public int getPosCount(String tableNm, String time) {
		return mailDao.getPosCount(tableNm, time);
	}

	@Override
	public int getErpCount(String tableNm, String time) {
		return mailDao.getErpCount(tableNm, time);
	}

	@Override
	public String getTableName(String tableId) {
		return mailDao.getName(tableId);
	}

	@Override
	public List<Map<String, Object>> getPosTime(String tableId, String start, String end) {
		return mailDao.getPosTime(tableId, start, end);
	}

	@Override
	public List<Map<String, Object>> getErpTime(String tableId, String time, String time2, String time3) {
		return mailDao.getErpTime(tableId, time, time2, time3);
	}

	@Override
	public List<Map<String, Object>> getTableList() {
		return mailDao.getTableList();
	}

	@Override
	public List<Map<String, Object>> getOrgaReceivers() {
		return mailDao.getOrgaReceivers();
	}	
	
	@Override
	public List<Map<String, Object>> getReceivers() {
		return mailDao.getReceivers();
	}

	@Override
	public Map<String, Object> getOrgaCount(String start, String term, String date, String end) {
		return mailDao.getOrgaCount(start, term, date, end);
	}

}
