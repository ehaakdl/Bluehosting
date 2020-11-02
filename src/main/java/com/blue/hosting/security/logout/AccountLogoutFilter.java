package com.blue.hosting.security.logout;

import org.springframework.security.web.authentication.logout.LogoutFilter;


public class AccountLogoutFilter extends LogoutFilter {
    public AccountLogoutFilter(String logoutSuccessUrl, AccountLogoutHandler handlers) {
        super(logoutSuccessUrl, handlers);
    }
}
