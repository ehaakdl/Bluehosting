package com.blue.hosting.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.LinkedList;

public class CustomProviderManager implements AuthenticationManager {
    private LinkedList<AuthenticationProvider> mProviders;

    public CustomProviderManager(LinkedList<AuthenticationProvider> mProviders){
        this.mProviders = mProviders;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Class<? extends Authentication> classCompare = authentication.getClass();
        Authentication resultAuth = null;
        for (AuthenticationProvider mProvider : mProviders) {
            if(mProvider.supports(classCompare)){
                try {
                    resultAuth = mProvider.authenticate(authentication);
                }catch(UsernameNotFoundException except){
                    throw except;
                }catch (BadCredentialsException except){
                    throw except;
                }
                break;
            }
        }
        return resultAuth;
    }
}
