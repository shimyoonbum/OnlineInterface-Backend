package com.pulmuone.OnlineIFServer.service;

import java.util.List;
import java.util.Map;

public interface CommonService {
	public List<Map> systems();

	public List<Map> interfaces(Map<String, Object> param);

	public List<Map> metas(Map<String, Object> param);

	public List<Map> metaAttrs(Map<String, Object> param);

	public void log(Map<String, Object> param);

	public String findDummy(Map<String, Object> param);

}
