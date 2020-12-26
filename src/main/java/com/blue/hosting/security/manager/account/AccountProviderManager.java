package com.blue.hosting.security.manager.account;

import com.blue.hosting.security.exception.eAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.*;

public class AccountProviderManager extends ProviderManager {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    public AccountProviderManager(List<AuthenticationProvider> mProviders){
        super(mProviders);
    }
    private Authentication getAuthentication(AuthenticationProvider provider, Authentication authentication) throws AuthenticationException{
        Authentication token;
        try {
            token = provider.authenticate(authentication);
        }catch(UsernameNotFoundException except){
            throw new RuntimeException(except);
        }catch (BadCredentialsException except){
            throw new RuntimeException(except);
        }catch(AuthenticationException except){
            throw new RuntimeException(except);
        }

        return token;
    }
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        List<AuthenticationProvider> providers = super.getProviders();
        Class<? extends Authentication> classCompare = authentication.getClass();
        Authentication token = authentication;
        boolean bSearch = false;
        for (AuthenticationProvider mProvider : providers) {
            if(mProvider.supports(classCompare)){
                bSearch = true;
                token = getAuthentication(mProvider, authentication);
                break;
            }
        }

        if(bSearch == false){
            eAuthenticationException exceptCode = eAuthenticationException.PROVIDER_NOT_FOUND;
            logger.error(exceptCode.getMsg());
            throw new ProviderNotFoundException(exceptCode.getMsg());
        }
        return token;
    }
}
