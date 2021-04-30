package com.pulmuone.OnlineIFServer.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommonDao {
	public List<Map> systems();

	public List<Map> interfaces(Map<String, Object> param);

	public List<Map> metas(Map<String, Object> param);

	public List<Map> metaAttrs(Map<String, Object> param);

	public void log(Map<String, Object> param);

	public String findDummy(Map<String, Object> param);
}
