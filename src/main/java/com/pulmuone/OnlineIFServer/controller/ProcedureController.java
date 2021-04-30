package com.pulmuone.OnlineIFServer.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pulmuone.OnlineIFServer.util.ProcUtil;

@RestController
@RequestMapping("/procedure")
public class ProcedureController {

	@Autowired
	ProcUtil procUtil;

	@PostMapping(value = "/interface")
    public void interfaces(HttpServletRequest request, HttpServletResponse response) {
		procUtil.interfaces(request, response);
    }

}
