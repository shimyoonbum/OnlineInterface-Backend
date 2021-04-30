package com.pulmuone.OnlineIFServer.controller;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pulmuone.OnlineIFServer.common.ResponseStatus;
import com.pulmuone.OnlineIFServer.service.UserService;
import com.pulmuone.OnlineIFServer.util.RestUtil;

@RestController
@RequestMapping("/user")
public class UserRestController {

	@Autowired
	RestUtil restUtil;

	@Autowired
	UserService userService;
	
    @GetMapping
    public ResponseEntity<?> getAllUsers(HttpServletRequest request){
    	ResponseEntity<?> entity = restUtil.verifyRequest(request, null);
    	if(entity!=null)
    		return entity;
    	
    	Map map = new HashMap();
    	List<Map> list = userService.getUser();
    	map.put("list", list);
    	return restUtil.getResponse(null, map, request, ResponseStatus.OK, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Integer id, HttpServletRequest request) {
    	ResponseEntity<?> entity = restUtil.verifyRequest(request, null);
    	if(entity!=null)
    		return entity;
    	
    	Map map = userService.findById(id);
        if (map == null) {
            return restUtil.getResponse(null, new HashMap(), request, ResponseStatus.FAIL, HttpStatus.NOT_FOUND);
        }
        return restUtil.getResponse(null, map, request, ResponseStatus.OK, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> param, HttpServletRequest request) throws SQLIntegrityConstraintViolationException {
    	ResponseEntity<?> entity = restUtil.verifyRequest(request, param);
    	if(entity!=null)
    		return entity;
    	
        if (userService.findById(new Long(param.get("id").toString())) != null) {
        	return restUtil.getResponse(param, new HashMap(), request, ResponseStatus.DUP, HttpStatus.IM_USED);
            //return new ResponseEntity<String>("Duplicate Entry "+ param.get("id"), HttpStatus.IM_USED);
        }
        userService.saveUser(param);
    	return restUtil.getResponse(param, new HashMap(), request, ResponseStatus.OK, HttpStatus.CREATED);
        //return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody Map<String, Object> param, HttpServletRequest request) {	//User user
    	ResponseEntity<?> entity = restUtil.verifyRequest(request, param);
    	if(entity!=null)
    		return entity;
    	
    	Map map = userService.findById(new Long(param.get("id").toString()));
        if (map == null) {
        	return restUtil.getResponse(param, new HashMap(), request, ResponseStatus.NOT_FOUND, HttpStatus.NOT_FOUND);
            //return new ResponseEntity<String>("Unable to delete as  User id " + id + " not found.", HttpStatus.NOT_FOUND);
        }
        userService.updateUser(param);
    	return restUtil.getResponse(param, map, request, ResponseStatus.OK, HttpStatus.CREATED);
        //return new ResponseEntity<Map<String, Object>>(param, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Integer id, HttpServletRequest request) {
    	ResponseEntity<?> entity = restUtil.verifyRequest(request, null);
    	if(entity!=null)
    		return entity;
    	
    	Map map = userService.findById(id);
        if (map == null) {
        	return restUtil.getResponse(null, new HashMap(), request, ResponseStatus.NOT_FOUND, HttpStatus.NOT_FOUND);
            //return new ResponseEntity<String>("Unable to delete as  User id " + id + " not found.", HttpStatus.NOT_FOUND);
        }
        userService.deleteUserById(id);
    	return restUtil.getResponse(null, new HashMap(), request, ResponseStatus.OK, HttpStatus.OK);
        //return new ResponseEntity<Map>(HttpStatus.NO_CONTENT);
    }

}
