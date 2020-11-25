package com.blue.hosting.security.filter.account;

import com.blue.hosting.entity.account.AccountInfoVO;
import com.blue.hosting.security.authentication.account.JwtCertificationToken;
import com.blue.hosting.security.exception.CustomAuthenticationException;
import com.blue.hosting.security.exception.eAuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
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
        JwtCertificationToken authToken = (JwtCertificationToken)SecurityContextHolder.getContext().getAuthentication();
        if(authToken != null){
            throw new CustomAuthenticationException(eAuthenticationException.ALREADY_CERTIFIED);
        }

        UsernamePasswordAuthenticationToken token = null;
        try{
            AccountInfoVO accountInfoVO = new ObjectMapper().readValue(request.getInputStream(), AccountInfoVO.class);
            token = new UsernamePasswordAuthenticationToken(accountInfoVO.getUsername(), accountInfoVO.getPassword());
        } catch (IOException except) {
            CustomAuthenticationException e = new CustomAuthenticationException(eAuthenticationException.OBJECT_READ_FAIL);
            throw e;
        }

        setDetails(request, token);
        return this.getAuthenticationManager().authenticate(token);
    }
}
