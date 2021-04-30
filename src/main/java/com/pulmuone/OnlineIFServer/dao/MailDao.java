package com.pulmuone.OnlineIFServer.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MailDao {
	public int getPosCount(String table, String time);

	public int getErpCount(String table, String time);

	public String getName(String tableId);

	public List<Map<String, Object>> getPosTime(String table, String start, String end);

	public List<Map<String, Object>> getErpTime(String table, String time, String start, String end);

	public List<Map<String, Object>> getTableList();

	public List<Map<String, Object>> getReceivers();

	public Map<String, Object> getOrgaCount(String start, String term, String date, String end);

	public List<Map<String, Object>> getOrgaReceivers();
}
