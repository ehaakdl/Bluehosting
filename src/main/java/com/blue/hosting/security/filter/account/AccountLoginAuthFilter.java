package com.blue.hosting.security.filter.account;

import com.blue.hosting.entity.account.AccountInfoVO;
import com.blue.hosting.security.authentication.account.JwtCertificationToken;
import com.blue.hosting.security.exception.CustomAuthenticationException;
import com.blue.hosting.security.exception.eAuthenticationException;
import com.blue.hosting.security.exception.eSystemException;
import com.blue.hosting.service.account.eCustomResponseCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccountLoginAuthFilter extends UsernamePasswordAuthenticationFilter {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    public AccountLoginAuthFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        if(SecurityContextHolder.getContext().getAuthentication() != null){
            throw new CustomAuthenticationException(eAuthenticationException.ALREADY_CERTIFIED);
        }

        UsernamePasswordAuthenticationToken token = null;
        try{
            AccountInfoVO accountInfoVO = new ObjectMapper().readValue(request.getInputStream(), AccountInfoVO.class);
            token = new UsernamePasswordAuthenticationToken(accountInfoVO.getUsername(), accountInfoVO.getPassword());
        } catch (IOException except) {
            throw new CustomAuthenticationException(eAuthenticationException.OBJECT_READ_FAIL);
        }

        setDetails(request, token);
        return getAuthenticationManager().authenticate(token);
    }
}