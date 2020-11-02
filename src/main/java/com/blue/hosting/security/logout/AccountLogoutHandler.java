package com.blue.hosting.security.logout;

import com.blue.hosting.utils.token.ClientTokenMange;
import com.blue.hosting.utils.token.eTokenVal;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AccountLogoutHandler implements LogoutHandler {
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if(!ClientTokenMange.bHaveToken(request.getCookies())){
            return;
        }



    }
}
