package com.blue.hosting.controller;

import com.blue.hosting.service.email.EmailManagement;
import com.blue.hosting.utils.ResponseStatusCode;
import lombok.Getter;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
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
            res.setStatus(ResponseStatusCode.EMAIL_REQ_FAIL);
            return;
        }
    }

    @PostMapping("/code/check")
    public void check(HttpServletResponse res, @RequestBody Map<String, Object> data){
        int code = new Integer((String)data.get(CODE));
        String email = (String)data.get(EMAIL);

        if(mEmailManagement.isCode(code, email) == false){
            res.setStatus(ResponseStatusCode.WRONG_CODE);
        }
    }
}
