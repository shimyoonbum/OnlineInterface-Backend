package com.pulmuone.OnlineIFServer.config;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.pulmuone.OnlineIFServer.service.CommonService;
import com.pulmuone.OnlineIFServer.util.RegExHashMap;
 
@Component
public class SystemConfig {
	@Autowired
	CommonService commonService;

    private static RegExHashMap systemMap = null;
    
	long startTime = 0L;
	long endTime = 0L;	
    
    public RegExHashMap getSystemMap() {
    	if(systemMap == null) {
    		systemMap = new RegExHashMap();
    	
        	List<Map> list = commonService.systems();
        	for(Map map : list)
        		systemMap.put(map.get("authkey").toString(), map);
    	}
    	return systemMap;
    }
    
    synchronized public Map getSystemInfo(String key) {
    	return (Map) getSystemMap().get(key);
    }
    
    //2020.12.08 시작시간 측정 메소드
    public void setTime() {
    	startTime = System.nanoTime();
    }
    
    //2020.12.08 통신종료시간 측정 메소드. IFLOG 테이블에 로그 기록 직전까지.
    public double getTime() {
    	endTime = System.nanoTime();
    	
    	long estimatedTime = endTime - startTime;
    	// nano seconds to seconds
    	return estimatedTime / 1000000000.0;    	
    }    
}
