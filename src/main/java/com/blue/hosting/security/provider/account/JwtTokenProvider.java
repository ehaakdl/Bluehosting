package com.blue.hosting.security.provider.account;

import com.blue.hosting.security.authentication.account.JwtCertificationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

public class JwtTokenProvider implements AuthenticationProvider {
    private final String CLAIMS_NAME = "id";

    @Override
    public Authentication authenticate(Authentication authentication)  throws AuthenticationException{
    /*    JwtCertificationToken token = (JwtCertificationToken) authentication;
        JwtTokenManagement jwtTokenManagement = new JwtTokenManagement();
        Cookie cookie = (Cookie) token.getmCookie();
        Map claims = jwtTokenManagement.getClaims(cookie.getValue());
        if(jwtTokenManagement.verify(cookie.getValue()) == false){
            eAuthenticationException e = eAuthenticationException.TOKEN_NOT_VERIFY;
            throw new BadCredentialsException(e.getMsg());
        }
        if(claims == null){
            //재발급
        }

            String id = (String)claims.get(CLAIMS_NAME);
    */
        //token.setId(id);
        //SecurityContextHolder.getContext().setAuthentication(null);


        //UsernamePasswordAuthenticationToken currentUser = new UsernamePasswordAuthenticationToken(id,null);
        //SecurityContextHolder.getContext().setAuthentication(currentUser);
        return authentication;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        if(aClass != JwtCertificationToken.class){
            return false;
        }
        return true;
    }
}
