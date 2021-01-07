package com.blue.hosting.controller;

import com.blue.hosting.entity.email.EmailStateDAO;
import com.blue.hosting.entity.email.EmailStateRepo;
import com.blue.hosting.service.email.EmailManagement;
import com.blue.hosting.utils.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/email")
public class EmailController {
    private final String EMAIL = "email";

    private final String CODE = "code";

    @Resource(name="emailManagement")
    public void setEmailManagement(EmailManagement emailManagement) {
        this.mEmailManagement = emailManagement;
    }

    private EmailManagement mEmailManagement;

    @PostMapping("/code/request")
    public void request(HttpServletResponse res, @RequestBody String email){
        try{
            mEmailManagement.insert(email);
        }catch (RuntimeException e){
            res.setStatus(HttpStatusCode.EMAIL_REQ_FAIL);
            return;
        }
    }

    @PostMapping("/code/check")
    public void check(@RequestBody Map<String, Object> map){
        int code = (int)map.get(CODE);
        String email = (String)map.get(EMAIL);
    }
}
