package com.blue.hosting.security.login;

import com.blue.hosting.entity.account.AccountInfoVO;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        String reqId = (String) authentication.getPrincipal();
        String reqPassword = (String) authentication.getCredentials();
        AccountInfoVO accountInfo = null;
        try {
            accountInfo = mAccountLoginAuthService.loadUserByUsername(reqId);
        } catch(UsernameNotFoundException except){
            throw except;
        }

        boolean bResult = comparePassword(reqPassword, accountInfo.getPassword());
        if(!bResult) {
            throw new BadCredentialsException(reqId);
        }
        
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) authentication;
        return authToken;
    }

    private boolean comparePassword(String reqPassword, String password) {
        boolean bResult = mPasswordEncoder.matches(reqPassword, password);
        return bResult;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        Class<UsernamePasswordAuthenticationToken> authentication = UsernamePasswordAuthenticationToken.class;
        if(aClass.getClass() != authentication.getClass()){
            return false;
        }
        return true;
    }
}
