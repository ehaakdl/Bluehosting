package com.blue.hosting.security.login;

import com.blue.hosting.entity.account.AccountInfoVO;
import com.blue.hosting.utils.eExceptionCode;
import com.blue.hosting.utils.token.ClientTokenMange;
import com.blue.hosting.utils.token.eTokenVal;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccountLoginAuthFilter extends UsernamePasswordAuthenticationFilter {
    public AccountLoginAuthFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        eTokenVal tokenType = eTokenVal.ACCESS_TOKEN;
        if(ClientTokenMange.isSearch(tokenType.getmTokenType(), request.getCookies())){
            throw new RuntimeException();
        }

        UsernamePasswordAuthenticationToken authRequest = null;
        try{
            AccountInfoVO accountInfoVO = new ObjectMapper().readValue(request.getInputStream(), AccountInfoVO.class);
            authRequest = new UsernamePasswordAuthenticationToken(accountInfoVO.getUsername(), accountInfoVO.getPassword());
        } catch (RuntimeException | IOException except) {
            eExceptionCode exceptCode = eExceptionCode.OBJECT_READ_FAIL;
            throw new RuntimeException();
        }
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
