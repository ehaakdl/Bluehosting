package com.blue.hosting.security.authentication.account;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public class JwtCertificationToken implements Authentication {
    public Cookie getmCookie() {
        return mCookie;
    }

    public HttpServletResponse getmRes() {
        return mRes;
    }

    private Cookie mCookie;
    private HttpServletResponse mRes;

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    public JwtCertificationToken(Cookie cookie, HttpServletResponse res) {
        this.mCookie = cookie;
        this.mRes = res;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.id;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {
    }


    @Override
    public String getName() {
        return this.id;
    }
}
