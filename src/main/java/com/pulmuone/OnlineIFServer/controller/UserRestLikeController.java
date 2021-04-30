package com.pulmuone.OnlineIFServer.controller;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pulmuone.OnlineIFServer.service.CommonService;
import com.pulmuone.OnlineIFServer.service.UserService;
import com.pulmuone.OnlineIFServer.util.ProcUtil;

@RestController
@RequestMapping("/restlike/user")
public class UserRestLikeController {

	@Autowired
	ProcUtil procUtil;

	@Autowired
	CommonService commonService;

	@Autowired
	UserService userService;

    @GetMapping
    public void getAllUsers(HttpServletRequest request, HttpServletResponse response){
		procUtil.interfaces(request, response);
    }

    @GetMapping(value = "/{id}")
    public void getUser(@PathVariable("id") Integer id, HttpServletRequest request, HttpServletResponse response) {
    	Map<String,Object> resourceMap = new HashMap<String,Object>();
    	resourceMap.put("id", id);
    	
		procUtil.interfaces(request, response, resourceMap);
    }

    @PostMapping
    public void createUser(HttpServletRequest request, HttpServletResponse response) throws SQLIntegrityConstraintViolationException {
		procUtil.interfaces(request, response);
    }


    @PutMapping
    public void updateUser(HttpServletRequest request, HttpServletResponse response) {
		procUtil.interfaces(request, response);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteUser(@PathVariable("id") Integer id, HttpServletRequest request, HttpServletResponse response) {
    	Map<String,Object> resourceMap = new HashMap<String,Object>();
    	resourceMap.put("id", id);
    	
		procUtil.interfaces(request, response, resourceMap);
    }

}
