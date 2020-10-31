package com.blue.hosting.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class mainController {
	
	@RequestMapping("/test")
	public String index(HttpServletRequest request, HttpServletResponse response) {
		return "redirect:index";
	}
	@RequestMapping("/")
	public String test(HttpServletRequest request, HttpServletResponse response) {
		return "index";
	}
}
