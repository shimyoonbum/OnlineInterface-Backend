package com.pulmuone.OnlineIFServer.service;

import java.util.List;
import java.util.Map;

import com.pulmuone.OnlineIFServer.common.IFException;
import com.pulmuone.OnlineIFServer.util.CamelListMap;

public interface MailService {
	public int getPosCount(String tableNm, String time);

	public int getErpCount(String tableNm, String time);

	public String getTableName(String tableId);

	public List<Map<String, Object>> getPosTime(String tableId, String time2, String time3);

	public List<Map<String, Object>> getErpTime(String tableId, String time, String time2, String time3);

	public List<Map<String, Object>> getTableList();

	public List<Map<String, Object>> getReceivers();

	public Map<String, Object> getOrgaCount(String today, String medium, String time, String tommorow);

	public List<Map<String, Object>> getOrgaReceivers();
}
