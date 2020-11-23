package com.blue.hosting.security.manager.account;

import com.blue.hosting.security.exception.eAuthenticationException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.*;

public class AccountProviderManager extends ProviderManager {

    public AccountProviderManager(List<AuthenticationProvider> mProviders){
        super(mProviders);
    }
    private Authentication getSupport(AuthenticationProvider provider, Authentication authentication) throws AuthenticationException{
        Authentication token;
        try {
            token = provider.authenticate(authentication);
        }catch(UsernameNotFoundException except){
            throw except;
        }catch (BadCredentialsException except){
            throw except;
        }

        return token;
    }
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        List<AuthenticationProvider> providers = super.getProviders();
        Class<? extends Authentication> classCompare = authentication.getClass();
        Authentication token = null;
        boolean bSearch = false;
        for (AuthenticationProvider mProvider : providers) {
            if(mProvider.supports(classCompare)){
                bSearch = true;
                token = getSupport(mProvider, authentication);
                break;
            }
        }

        if(bSearch == false){
            eAuthenticationException exceptCode = eAuthenticationException.PROVIDER_NOT_FOUND;
            throw new ProviderNotFoundException(exceptCode.getMsg());
            //log
        }
        return token;
    }
}
