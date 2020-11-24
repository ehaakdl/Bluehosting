package com.blue.hosting.security.filter.account;

import com.blue.hosting.security.authentication.account.JwtCertificationToken;
import com.blue.hosting.security.handler.account.AccountLogoutHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutFilter;


public class AccountLogoutFilter extends LogoutFilter {
    public AccountLogoutFilter(String logoutSuccessUrl, AccountLogoutHandler handlers) {
        super(logoutSuccessUrl, handlers);
    }

    @Override
    public void setFilterProcessesUrl(String filterProcessesUrl) {
        super.setFilterProcessesUrl(filterProcessesUrl);
    }
}
