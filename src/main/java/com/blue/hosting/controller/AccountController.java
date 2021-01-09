package com.blue.hosting.controller;



import com.blue.hosting.entity.account.AccountInfoVO;
import com.blue.hosting.service.account.AccountManagement;
import com.blue.hosting.utils.PageIndex;
import com.blue.hosting.utils.ResponseStatusCode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController()
@RequestMapping("/account")
public class AccountController {
    @Resource(name="accountManagement")
    public void setAccountSignUp(AccountManagement accountManagement) {
        this.accountManagement = accountManagement;
    }

    private AccountManagement accountManagement;

    private boolean sendRedirect(HttpServletResponse response, String url){
        try{
            response.sendRedirect(url);
        }catch (IOException except){
            return false;
        }
        return true;
    }

    @PostMapping("/signup")
    public void signUp(@RequestBody AccountInfoVO accountInfoVO,
                       HttpServletRequest request, HttpServletResponse response) {
        if(accountManagement.findById(accountInfoVO.getId())){
            response.setStatus(ResponseStatusCode.OVERLAP_ID);
            return;
        }
        try{
            accountManagement.create(accountInfoVO);
        }catch (Exception e){
            response.setStatus(ResponseStatusCode.FAIL_SIGNUP);
            return;
        }

        sendRedirect(response, PageIndex.REDIRECT_INDEX);
    }
}
