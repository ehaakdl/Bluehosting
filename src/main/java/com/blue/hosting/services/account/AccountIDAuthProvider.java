package com.blue.hosting.services.account;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


public class AccountIDAuthProvider implements AuthenticationProvider {
    private BCryptPasswordEncoder mPasswordEncoder;

    public AccountIDAuthProvider(BCryptPasswordEncoder passwordEncoder){
        this.mPasswordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) authentication;
        String reqId = (String) authentication.getPrincipal();
        String reqPassword = (String) authentication.getCredentials();

        AccountAuthService accountAuthService = new AccountAuthService();
        AccountInfoVO accountInfo = null;
        eSecurityErrorMessage securityErrorMsg = null;
        try {
            accountInfo = accountAuthService.loadUserByUsername(reqId);
        } catch(UsernameNotFoundException except){
            //securityErrorMsg = eSecurityErrorMessage.INPUT_NOT_FOUND;
            //log 삽입
            throw except;
        }

        boolean bResult = comparePassword(reqPassword, accountInfo.getPassword());
        if(!bResult) {
            throw new BadCredentialsException(reqId);
        }

        return authToken;
    }

    private boolean comparePassword(String reqPassword, String password) {
        boolean bResult = reqPassword.equals(password);
        return bResult;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        //return aClass.equals(Authentication.class);
        return true;
    }
}
