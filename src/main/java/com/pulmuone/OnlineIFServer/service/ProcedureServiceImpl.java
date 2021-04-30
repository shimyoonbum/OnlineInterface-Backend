package com.pulmuone.OnlineIFServer.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProcedureServiceImpl implements ProcedureService {
    @Autowired
    JdbcTemplate jdbcTemplate;

	public String interfaceFunction(Map<String, Object> reqMap) {
		//param.put("interfaceFunction", "test_func('')");
		Map<String,Object> commonMap = (Map<String,Object>) reqMap.get("common_map");
		if(commonMap.get("procNm") == null)
			return null;
		
		String sql = "select "+commonMap.get("procNm")+"(?, ?) FROM dual";
		
		return jdbcTemplate.queryForObject(sql, new Object[]{(String) reqMap.get("common_string"), (String) reqMap.get("param_string")}, String.class);
	}
}
