package com.pulmuone.OnlineIFServer.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pulmuone.OnlineIFServer.config.DBCleanConfig;
import com.pulmuone.OnlineIFServer.service.BackupService;
import com.pulmuone.OnlineIFServer.service.CommonUIService;
import com.pulmuone.OnlineIFServer.service.MetaReloadService;
import com.pulmuone.OnlineIFServer.service.TriggerService;
import com.pulmuone.OnlineIFServer.util.CamelListMap;
import com.pulmuone.OnlineIFServer.util.ProcUtil;

@RestController
@CrossOrigin(origins = "*", allowCredentials = "true")
@RequestMapping("/common")
//@Api(value = "CommonController", description = "각종 정보들을 가져오는 컨트롤러")
public class CommonController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ProcUtil procUtil;

	@Autowired
	CommonUIService commonUIService;

	@Autowired
	MetaReloadService metaReloadService;

	@Autowired
	TriggerService triggerService;

	@Autowired
	BackupService backupService;

	@Autowired
	DBCleanConfig dbCleanConfig;

	@PostMapping(value = "/getSystems")
    public ResponseEntity<?> getSystems(@RequestBody Map<String, Object> param, HttpServletRequest request) {
    	Map map = new HashMap();
    	List<Map> list = commonUIService.getSystems(param);
        if (list == null)
            return new ResponseEntity<Map>(new HashMap(), HttpStatus.NOT_FOUND);
    	map.put("data", list);
    	return new ResponseEntity<Map>(map, HttpStatus.OK);
    }

	@PostMapping(value = "/getInterfaces")
    public ResponseEntity<?> getInterfaces(@RequestBody Map<String, Object> param, HttpServletRequest request) {
    	Map map = new HashMap();
    	List<Map> list = commonUIService.getInterfaces(param);
        if (list == null)
            return new ResponseEntity<Map>(new HashMap(), HttpStatus.NOT_FOUND);
    	map.put("data", list);
    	return new ResponseEntity<Map>(map, HttpStatus.OK);
    }

	@PostMapping(value = "/getLogs")
    public ResponseEntity<?> getLogs(@RequestBody Map<String, Object> param, HttpServletRequest request) {
    	Map map = new HashMap();
    	List<Map> list = commonUIService.getLogs(param);
        if (list == null)
            return new ResponseEntity<Map>(new HashMap(), HttpStatus.NOT_FOUND);
    	map.put("data", list);
    	int total = commonUIService.getLogCount(param);
    	map.put("total", total);
    	return new ResponseEntity<Map>(map, HttpStatus.OK);
    }

	@PostMapping(value = "/getLog")
    public ResponseEntity<?> getLog(@RequestBody Map<String, Object> param, HttpServletRequest request) {
    	Map map = commonUIService.getLog(param);
        if (map == null)
            return new ResponseEntity<Map>(new HashMap(), HttpStatus.NOT_FOUND);
    	return new ResponseEntity<Map>(map, HttpStatus.OK);
    }

	@PostMapping(value = "/getDummies")
    public ResponseEntity<?> getDummy(@RequestBody Map<String, Object> param, HttpServletRequest request) {
    	Map map = new HashMap();
    	List<Map> list = commonUIService.getDummies(param);
        if (list == null)
            return new ResponseEntity<Map>(new HashMap(), HttpStatus.NOT_FOUND);
    	map.put("data", list);
    	return new ResponseEntity<Map>(map, HttpStatus.OK);
    }

	@PostMapping(value = "/dummy")
    public ResponseEntity<?> insertDummy(@RequestBody Map<String, Object> param, HttpServletRequest request) {
    	Map map = new HashMap();
    	commonUIService.insertDummy(param);
    	map.put("ifSeq", param.get("if_seq"));
    	return new ResponseEntity<Map>(map, HttpStatus.OK);
    }

	@PutMapping(value = "/dummy")
    public ResponseEntity<?> updateDummy(@RequestBody Map<String, Object> param, HttpServletRequest request) {
    	Map map = new HashMap();
    	commonUIService.updateDummy(param);
    	return new ResponseEntity<Map>(map, HttpStatus.OK);
    }

	@DeleteMapping(value = "/dummy")
    public ResponseEntity<?> deleteDummy(@RequestBody Map<String, Object> param, HttpServletRequest request) {
    	Map map = new HashMap();
    	commonUIService.deleteDummy(param);
    	return new ResponseEntity<Map>(map, HttpStatus.OK);
    }

	/**** Meta reload****/
	@PostMapping(value = "/meta")
	//@ApiOperation(value="meta reload", notes="메타 정보 리로드")	
    public ResponseEntity<?> reloadMeta(@RequestBody Map<String, Object> param, HttpServletRequest request) {
    	Map map = new HashMap();
    	String errMsg = "";
    	try {
    		errMsg = metaReloadService.reloadMeta(param);
		} catch (Exception e) {
			map.put("responseMessage", e.getMessage());
			return new ResponseEntity<Map>(map, HttpStatus.BAD_REQUEST);
		}
		map.put("responseMessage", errMsg);
    	return new ResponseEntity<Map>(map, HttpStatus.OK);
    }

	/**** Create Trigger****/
	@PostMapping(value = "/trigger")
    public ResponseEntity<?> createTrigger(@RequestBody Map<String, Object> param, HttpServletRequest request) {
    	Map map = new HashMap();
    	String errMsg = "";
    	try {
    		triggerService.createTrigger(param);
		} catch (Exception e) {
			map.put("responseMessage", e.getMessage());
			return new ResponseEntity<Map>(map, HttpStatus.BAD_REQUEST);
		}
		map.put("responseMessage", errMsg);
    	return new ResponseEntity<Map>(map, HttpStatus.OK);
    }

	/**** Get Backup****/
	@PostMapping(value = "/backup")
    public ResponseEntity<?> getBackup(@RequestBody Map<String, Object> param, HttpServletRequest request) {
    	Map map = new HashMap();
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	List<CamelListMap> list2 = new ArrayList<CamelListMap>();
    	Map<String, Object> count = new HashMap<String, Object>();
    	String errMsg = "";
    	try {
    		list = backupService.selectBackup(param);
    		list2 = backupService.selectBackupColList(param);
    		count = backupService.selectBackupCountSql(param);
		} catch (Exception e) {
			map.put("responseMessage", e.getMessage());
			return new ResponseEntity<Map>(map, HttpStatus.BAD_REQUEST);
		}    	
    	
    	map.put("data", list);
    	map.put("data2", list2);
    	map.put("total", count);
		map.put("responseMessage", errMsg);
    	return new ResponseEntity<Map>(map, HttpStatus.OK);
    }
	
	/**** Do Backup****/
	@PostMapping(value = "/recovery")
    public ResponseEntity<?> doRecovery(@RequestBody Map<String, Object> param, HttpServletRequest request) {
    	Map map = new HashMap();
    	try {
    		backupService.doRecovery(param);
		} catch (Exception e) {
			map.put("responseMessage", e.getMessage());
			return new ResponseEntity<Map>(map, HttpStatus.BAD_REQUEST);
		}    	    	
    	return new ResponseEntity<Map>(map, HttpStatus.OK);
    }
	
	/**** Get Table list ****/
	@GetMapping(value = "/getTable")
    public List<Map<String, Object>> getTables() {
		Map infoMap = dbCleanConfig.getInfo();
		
		if(infoMap == null)
			return null;

		Map cleanTables = (Map) infoMap.get(DBCleanConfig.TABLES);
		
		List<Map<String, Object>> list = new ArrayList<>();
				
		for(int i=0; i < cleanTables.size(); i++) {
			Map tableMap = (Map) cleanTables.get(""+i);
			Map<String, Object> tables = new HashMap<>();
			
			tables.put("name", tableMap.get(DBCleanConfig.TABLE).toString());
			list.add(tables);
		}
		
		return list;

	}
	
	/**** Get MainInfo****/
	@GetMapping(value = "/getMainInfo")
    public ResponseEntity<?> getMainInfo(HttpServletRequest request) {
    	Map map = new HashMap();
    	Map<String, String> list = new HashMap<>();
    	List<Map<String, String>> list2 = new ArrayList<Map<String, String>>();
    	List<Map<String, String>> list3 = new ArrayList<>();
    	String errMsg = "";
    	try {
    		list = commonUIService.getDate();    	
        	list2 = commonUIService.getSuccessCount(list); 
        	list3 = commonUIService.getFailCount(list);
		} catch (Exception e) {
			map.put("responseMessage", e.getMessage());
			return new ResponseEntity<Map>(map, HttpStatus.BAD_REQUEST);
		}    	
    	List<String> valueList = new ArrayList<>(list.values());   
    	map.put("data", valueList);
    	map.put("data2", list2);
    	map.put("data3", list3);
		map.put("responseMessage", errMsg);
    	return new ResponseEntity<Map>(map, HttpStatus.OK);
    }
	
	/**** Get MainInfo****/
	@PostMapping(value = "/getMainInfo")
    public ResponseEntity<?> getSysMainInfo(@RequestBody Map<String, Object> param, HttpServletRequest request) {
    	Map map = new HashMap();
    	Map<String, String> list = new HashMap<>();
    	List<Map<String, String>> list2 = new ArrayList<Map<String, String>>();
    	List<Map<String, String>> list3 = new ArrayList<>();
    	String errMsg = "";
    	try {
    		list = commonUIService.getDate();
    		list.put("system", param.get("system").toString());    		
        	list2 = commonUIService.getSuccessCount(list); 
        	list3 = commonUIService.getFailCount(list);
        	list.remove("system");
		} catch (Exception e) {
			map.put("responseMessage", e.getMessage());
			return new ResponseEntity<Map>(map, HttpStatus.BAD_REQUEST);
		}    	
    	List<String> valueList = new ArrayList<>(list.values());   
    	map.put("data", valueList);   	
    	map.put("data2", list2);  	
    	map.put("data3", list3);
    	logger.info(map.toString());    	
		map.put("responseMessage", errMsg);
    	return new ResponseEntity<Map>(map, HttpStatus.OK);
    }

}
