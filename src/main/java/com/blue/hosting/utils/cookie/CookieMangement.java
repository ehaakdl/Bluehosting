package com.blue.hosting.utils.cookie;

import com.blue.hosting.utils.token.eTokenVal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CookieMangement {
    public static void add(HttpServletResponse res, eCookie cookVal, String value){
        Cookie cook = new Cookie(cookVal.getName(), value);
        cook.setMaxAge(cookVal.getMaxAge());
        cook.setPath(cookVal.getPath());
        cook.setValue(value);
        res.addCookie(cook);
    }
    public static Cookie search(String name, Cookie[] cookies){
        if(cookies == null){
            return null;
        }
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals(name) == true){
                return cookie;
            }
        }
        return null;
    }

    public static void delete(HttpServletResponse response, eTokenVal tokenVal, Cookie[] cookies){
        Cookie cook = search(tokenVal.getmTokenType(), cookies);
        if(cook == null){
            return;
        }

        cook = new Cookie(tokenVal.getmTokenType(), null);
        cook.setMaxAge(0);
        cook.setPath("/");
        response.addCookie(cook);
    }
}
