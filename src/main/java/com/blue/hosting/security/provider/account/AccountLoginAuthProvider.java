package com.blue.hosting.security.provider.account;

import com.blue.hosting.entity.account.AccountInfoDAO;
import com.blue.hosting.entity.account.AccountInfoVO;
import com.blue.hosting.security.service.account.AccountLoginAuthService;
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
    public void setmAccountLoginAuthService(AccountLoginAuthService mAccountLoginAuthService) {
        this.mAccountLoginAuthService = mAccountLoginAuthService;
    }

    private AccountLoginAuthService mAccountLoginAuthService;
    private BCryptPasswordEncoder mPasswordEncoder;

    public AccountLoginAuthProvider(BCryptPasswordEncoder passwordEncoder) {
        this.mPasswordEncoder = passwordEncoder;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String id = (String) authentication.getPrincipal();
        String passwd = (String) authentication.getCredentials();
        AccountInfoDAO accountInfo = null;
        try {
            accountInfo = mAccountLoginAuthService.loadUserByUsername(id);
        } catch(UsernameNotFoundException except){
            throw except;
        }

        boolean bResult = comparePassword(passwd, accountInfo.getPassword());
        if(!bResult) {
            throw new BadCredentialsException(id);
        }
        
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        return token;
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
