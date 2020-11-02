package com.blue.hosting.security.login;

import com.blue.hosting.entity.TokenInfoRepo;
import com.blue.hosting.security.AccountInfoVO;
import com.blue.hosting.security.eSecurityVal;
import com.blue.hosting.utils.token.ClientTokenMange;
import com.blue.hosting.utils.token.JwtTokenHelper;
import com.blue.hosting.utils.token.eTokenVal;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccountLoginAuthFilter extends UsernamePasswordAuthenticationFilter {
    private TokenInfoRepo mTokenInfoRepo;

    @Resource(name="clientTokenMange")
    public void setmClientTokenMang(ClientTokenMange mClientTokenMang) {
        this.mClientTokenMang = mClientTokenMang;
    }

    private ClientTokenMange mClientTokenMang;

    @Resource(name="tokenInfoRepo")
    private void setmTokenInfoRepo(TokenInfoRepo tokenInfoRepo) {
        mTokenInfoRepo = tokenInfoRepo;
    }

    public AccountLoginAuthFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        if(ClientTokenMange.bHaveToken(request.getCookies()) == true){
            try {
                mClientTokenMang.refreshMange(request,response);
            }catch (Exception except){
                throw new RuntimeException();
            }
            throw new RuntimeException();
        }

        eTokenVal tokenType = eTokenVal.REFRESH_TOKEN;
        Cookie cook = ClientTokenMange.cookSearch(tokenType.getmTokenType(), request.getCookies());
        if(cook != null){
            throw new RuntimeException();
        }
        tokenType = eTokenVal.ACCESS_TOKEN;
        cook = ClientTokenMange.cookSearch(tokenType.getmTokenType(), request.getCookies());
        if(cook != null){
            throw new RuntimeException();
        }

        eSecurityVal securityVal;
        UsernamePasswordAuthenticationToken authRequest = null;
        try{
            AccountInfoVO accountInfoVO = new ObjectMapper().readValue(request.getInputStream(), AccountInfoVO.class);
            authRequest = new UsernamePasswordAuthenticationToken(accountInfoVO.getUsername(), accountInfoVO.getPassword());
        } catch (RuntimeException | IOException except) {
            securityVal = eSecurityVal.OBJECT_READ_FAIL;
            //로그 삽입
        }
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
