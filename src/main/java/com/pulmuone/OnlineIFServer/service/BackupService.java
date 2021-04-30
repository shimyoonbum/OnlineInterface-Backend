package com.pulmuone.OnlineIFServer.service;

import java.util.List;
import java.util.Map;

import com.pulmuone.OnlineIFServer.common.IFException;
import com.pulmuone.OnlineIFServer.util.CamelListMap;

public interface BackupService {
	public List<Map<String, Object>> selectBackup(Map<String, Object> param);

	public List<CamelListMap> selectBackupColList(Map<String, Object> param);

	public Map<String, Object> selectBackupCountSql(Map<String, Object> param);

	public void doRecovery(Map<String, Object> param) throws IFException;
}
