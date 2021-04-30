package com.pulmuone.OnlineIFServer.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pulmuone.OnlineIFServer.common.Constants;
import com.pulmuone.OnlineIFServer.service.CommonService;
import com.pulmuone.OnlineIFServer.util.RegExHashMap;
 
@Component
public class InterfaceConfig {
	@Autowired
	CommonService commonService;

    private static RegExHashMap interfaceMap = null;
    
    synchronized public Map getInterfaceInfo(String key) {
    	if(interfaceMap == null)
    		load("all");
    	
    	return (Map) interfaceMap.get(key);
    }
    
    synchronized public void load(String ifId) {
		Map<String, Object> param = new HashMap<String, Object>();
    	if(!ifId.equals("all"))
    		param.put("ifId", ifId);
    	
    	if(InterfaceConfig.interfaceMap == null)
    		ifId = "all";
    	
    	RegExHashMap interfaceMap = null;
    	if(ifId.equals("all")) {
    		interfaceMap = new RegExHashMap();
    	} else {
    		param.put(Constants.ifId, ifId);
    		interfaceMap = InterfaceConfig.interfaceMap;
    	}
		//RegExHashMap interfaceMap = new RegExHashMap();
    	List<Map> interfaces = commonService.interfaces(param);
    	for(Map map : interfaces) {
    		if(!ifId.equals("all") && !ifId.equals(map.get("ifId").toString()))
    			continue;
    		interfaceMap.put(map.get("systemId").toString()+"_"+map.get("ifId").toString(), map);
			//interfaceMap.put(map.get("method").toString()+map.get("uri").toString(), map);
    	}
    	
    	if(ifId.equals("all"))
    		InterfaceConfig.interfaceMap = interfaceMap;
	}
}
