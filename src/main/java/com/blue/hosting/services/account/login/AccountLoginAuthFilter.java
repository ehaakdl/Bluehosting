package com.blue.hosting.services.account.login;

import com.blue.hosting.services.account.AccountInfoVO;
import com.blue.hosting.services.account.eSecurityVal;
import com.blue.hosting.utils.token.JwtTokenHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccountLoginAuthFilter extends UsernamePasswordAuthenticationFilter {
    @Resource(name="jwtTokenHelper")
    private JwtTokenHelper mJwtTokenHelper;
    public AccountLoginAuthFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        UsernamePasswordAuthenticationToken authRequest = null;
        //mJwtTokenHelper.



        try{
            AccountInfoVO accountInfoVO = new ObjectMapper().readValue(request.getInputStream(), AccountInfoVO.class);
            authRequest = new UsernamePasswordAuthenticationToken(accountInfoVO.getUsername(), accountInfoVO.getPassword());
        } catch (RuntimeException | IOException except) {
            eSecurityVal securityVal = eSecurityVal.OBJECT_READ_FAIL;
            //로그 삽입
        }
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
