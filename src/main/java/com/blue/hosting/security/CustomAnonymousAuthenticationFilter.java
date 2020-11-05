package com.blue.hosting.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public class CustomAnonymousAuthenticationFilter extends AnonymousAuthenticationFilter {

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
    private String key;
    private Object principal;
    private List<GrantedAuthority> authorities;

    public CustomAnonymousAuthenticationFilter(String key) {
        this(key, ConstFilterValue.USER_ANONYMOUS, AuthorityUtils.createAuthorityList(ConstFilterValue.ROLE_ANONYMOUS));
    }

    public CustomAnonymousAuthenticationFilter(String key, Object principal, List<GrantedAuthority> authorities) {
        super(key);
        this.key = key;
        this.principal = principal;
        this.authorities = authorities;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if(securityContext.getAuthentication() == null){
            securityContext.setAuthentication(createAuthentication((HttpServletRequest)req));
        }

        chain.doFilter(req, res);
    }

    protected Authentication createAuthentication(HttpServletRequest request) {
        AnonymousAuthenticationToken authenticationToken = new AnonymousAuthenticationToken(key,
                principal, authorities);
        authenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));

        return authenticationToken;
    }
}
