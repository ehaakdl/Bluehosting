package com.blue.hosting.security.login;

import com.blue.hosting.security.AccountInfoVO;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;


public class AccountLoginAuthProvider implements AuthenticationProvider {
    @Resource(name = "accountLoginAuthSerivce")
    public void setmAccountAuthService(AccountLoginAuthService mAccountLoginAuthService) {
        this.mAccountLoginAuthService = mAccountLoginAuthService;
    }

    private AccountLoginAuthService mAccountLoginAuthService;
    private BCryptPasswordEncoder mPasswordEncoder;

    public AccountLoginAuthProvider(BCryptPasswordEncoder passwordEncoder) {
        this.mPasswordEncoder = passwordEncoder;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) authentication;
        String reqId = (String) authentication.getPrincipal();
        String reqPassword = (String) authentication.getCredentials();
        AccountInfoVO accountInfo = null;
        try {
            accountInfo = mAccountLoginAuthService.loadUserByUsername(reqId);
        } catch(RuntimeException except){
            throw except;
        }

        boolean bResult = comparePassword(reqPassword, accountInfo.getPassword());
        if(!bResult) {
            throw new BadCredentialsException(reqId);
        }

        return authToken;
    }

    private boolean comparePassword(String reqPassword, String password) {
        boolean bResult = mPasswordEncoder.matches(reqPassword, password);
        return bResult;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        //return aClass.equals(Authentication.class);
        return true;
    }
}
