package com.pulmuone.OnlineIFServer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TriggerServiceImpl implements TriggerService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    JdbcTemplate jdbcTemplate;

	public void createTrigger(Map<String, Object> param) {
		List<String> excludes = new ArrayList<String>();
		excludes.add("auth_log");
		excludes.add("auth_meta");
		excludes.add("iflog");
		
		String authlog = (String) param.get("authlog");
		List<String> list = (List<String>) param.get("tables");
		for (String table : list) {
			if(excludes.contains(table.toLowerCase()))
				continue;
			
			executeTrigger(table, authlog);
		}
	}

	private void executeTrigger(String table, String authlog) {
	    StringBuffer trigSB = new StringBuffer();
	    StringBuffer valuesSB = new StringBuffer();
	    String colNmStr = null;
	    String colNm = null;
	    String colType = null;
	    String nullable = null;
	    
	    trigSB.append("create or replace trigger trg_"+table+"\nafter insert or update or delete on "+table+" for each row\n");
	    trigSB.append("declare\n");
	    trigSB.append("\ttr_id varchar2(1);\n");
	    trigSB.append("\tjson varchar2(32767);\n");
	    trigSB.append("\tneed_comma boolean := false;\n");
	    trigSB.append("begin\n");

		trigSB.append("\ttr_id := case when inserting then 'I' when updating then 'U' when deleting then 'D' end;\n");

		valuesSB.append("\tjson := '{';\n");
		
		List<Map<String, Object>> list = jdbcTemplate.queryForList("select column_name,data_type,nullable from user_tab_columns where table_name = '"+table.toUpperCase()+"'");

		for (Map<String, Object> rec : list) {
			colNm = rec.get("column_name").toString().toLowerCase();
	    	colType = rec.get("data_type").toString().toLowerCase();
	    	nullable = rec.get("nullable").toString();
	    
			valuesSB.append("\tif tr_id = 'I' and :new"+"."+colNm+" is not null or tr_id = 'D' and '"+nullable+"' = 'N' "
						+"or tr_id = 'U' and ('"+nullable+"' = 'N' "
						+"or :old."+colNm+" is null and :new."+colNm+" is not null "
						+"or :new."+colNm+" is null and :old."+colNm+" is not null "
						+"or :new."+colNm+" != :old."+colNm+") \n");
			valuesSB.append("\tthen\n");
			valuesSB.append("\t\tif need_comma then json := json||','; else need_comma := true; end if;\n");
			valuesSB.append("\t\tjson := json||'\""+colNm+"\":");
		
			colNmStr = "case when tr_id = 'D' then :old."+colNm+" else :new."+colNm+" end";
			if(colType.equals("varchar2") || colType.equals("nvarchar2") || colType.equals("char") || colType.equals("nchar") || colType.equals("clob")) {
				valuesSB.append("\"'||"+colNmStr+"||'\"'");
			} else if(colType.equals("date")) {
				valuesSB.append("\"'||to_char("+colNmStr+",'YYYYMMDDHH24MISS')||'\"'");
			} else {
				valuesSB.append("'||"+colNmStr);
			}
			valuesSB.append(";\n");
			valuesSB.append("\tend if;\n");
		}
		valuesSB.append("\tjson := json||'}';\n");

	    trigSB.append(valuesSB.toString());
	   
	    trigSB.append("\n\tinsert into "+authlog+" (auth_date,tbl_nm,tr_id,ip_address,json)\n");
	    trigSB.append("\tvalues (sysdate, '"+table.toLowerCase()+"', tr_id, sys_context('userenv','ip_address'), json);\n");
	    trigSB.append("end;\n");
	    
	    logger.debug("\n"+trigSB.toString());
	    
	    jdbcTemplate.execute(trigSB.toString());
	}

}
