package com.pulmuone.OnlineIFServer.service;

import java.util.List;
import java.util.Map;

public interface CommonUIService {
	public List<Map> getSystems(Map<String, Object> param);

	public List<Map> getInterfaces(Map<String, Object> param);

	public int getLogCount(Map<String, Object> param);

	public List<Map> getLogs(Map<String, Object> param);

	public Map getLog(Map<String, Object> param);

	public List<Map> getDummies(Map<String, Object> param);

	public void insertDummy(Map<String, Object> param);

	public void updateDummy(Map<String, Object> param);

	public void deleteDummy(Map<String, Object> param);

	public Map<String, String> getDate();

	public List<Map<String, String>> getSuccessCount(Map<String, String> list);

	public List<Map<String, String>> getFailCount(Map<String, String> list);
}
