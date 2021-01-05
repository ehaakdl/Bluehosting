package com.blue.hosting.controller;



import com.blue.hosting.entity.account.AccountInfoVO;
import com.blue.hosting.service.account.AccountManagement;
import com.blue.hosting.service.account.eCustomResponseCode;
import com.blue.hosting.utils.PageIndex;
import org.springframework.ui.Model;
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
            response.setStatus(eCustomResponseCode.OVERLAP_ID.getResCode());
            return;
        }
        if(accountManagement.create(accountInfoVO) == false){
            response.setStatus(eCustomResponseCode.FAIL_SIGNUP.getResCode());
            return;
        }

        sendRedirect(response, PageIndex.REDIRECT_INDEX);
    }
}
