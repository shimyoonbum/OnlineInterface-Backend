package com.pulmuone.OnlineIFServer.common;

import java.util.Calendar;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pulmuone.OnlineIFServer.config.DBCleanConfig;

@Component
public class Scheduler {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	DBCleanConfig dbCleanConfig;

    @Scheduled(initialDelay = 6000,fixedDelay = 60000)
    public void fixedDelayJob() {
        try {
        	dbCleanConfig.clearTables();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
    }    
}
