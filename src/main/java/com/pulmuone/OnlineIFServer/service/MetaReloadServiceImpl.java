package com.pulmuone.OnlineIFServer.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pulmuone.OnlineIFServer.common.ResponseStatus;
import com.pulmuone.OnlineIFServer.config.InterfaceConfig;
import com.pulmuone.OnlineIFServer.config.MetaConfig;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MetaReloadServiceImpl implements MetaReloadService {

	@Autowired
	InterfaceConfig interfaceConfig;

	@Autowired
	MetaConfig metaConfig;

	public String reloadMeta(Map<String, Object> param) throws Exception {
		String ifId = (String) param.get("ifId");
		String errMsg = "";
		interfaceConfig.load(ifId);
		errMsg = metaConfig.load(ifId);
		
		return errMsg.length() > 0? errMsg : ResponseStatus.OK.phrase();
	}

}
