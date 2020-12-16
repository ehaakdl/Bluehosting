package com.blue.hosting.security.manager;

import com.blue.hosting.security.exception.eAuthenticationException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class ParentProviderManager extends ProviderManager {

    public ParentProviderManager(List<AuthenticationProvider> mProviders){
        super(mProviders);
    }

    /*
    인증 provider가 exception시 처리해주고 인증 성공하면 인증토큰 반환한다.
     */
    private Authentication getAuthentication(AuthenticationProvider provider, Authentication authentication) throws AuthenticationException{
        Authentication token;
        try {
            token = provider.authenticate(authentication);
        } catch(Exception except){
            throw except;
        }

        return token;
    }
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        List<AuthenticationProvider> providers = super.getProviders();
        Class<? extends Authentication> classCompare = authentication.getClass();
        Authentication token = authentication;
        for (AuthenticationProvider mProvider : providers) {
            if(mProvider.supports(classCompare)){
                token = getAuthentication(mProvider, authentication);
                break;
            }
        }

        return token;
    }
}
