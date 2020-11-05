package com.blue.hosting.controller;



import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController("/account")
public class AccountController {

    @RequestMapping(value="signup")
    public String signUp(HttpServletRequest request, HttpServletResponse response) {
        return "forward:/account/login";
    }
}
