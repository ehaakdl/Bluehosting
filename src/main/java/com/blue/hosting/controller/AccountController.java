package com.blue.hosting.controller;



import com.blue.hosting.entity.account.AccountInfoVO;
import com.blue.hosting.service.account.AccountMangement;
import com.blue.hosting.service.account.eCustomResponseCode;
import com.blue.hosting.utils.PageIndex;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController()
@RequestMapping("/account")
public class AccountController {
    @Resource(name="accountSignUp")
    public void setAccountSignUp(AccountMangement accountMangement) {
        this.accountMangement = accountMangement;
    }

    private AccountMangement accountMangement;

    private boolean sendError(HttpServletResponse response, int errorCode){
        try {
            response.sendError(errorCode);
        }catch (IOException except){
            //log
            return false;
        }
        return true;
    }

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
        eCustomResponseCode customResponseCode;
        eTokenVal tokenType = eTokenVal.ACCESS_TOKEN;
        if(ClientTokenMange.isSearch(tokenType.getmTokenType(), request.getCookies())){
            customResponseCode = eCustomResponseCode.ALREADY_LOGIN;
            sendError(response, customResponseCode.getResCode());
            return;
        }

        if(accountMangement.findById(accountInfoVO.getId())){
            customResponseCode = eCustomResponseCode.OVERLAP_ID;
            sendError(response, customResponseCode.getResCode());
            return;
        }

        if(!accountMangement.create(accountInfoVO)){
            customResponseCode = eCustomResponseCode.OVERLAP_ID;
            sendError(response, customResponseCode.getResCode());
            return;
        }
        sendRedirect(response, PageIndex.INDEX);
    }
}
