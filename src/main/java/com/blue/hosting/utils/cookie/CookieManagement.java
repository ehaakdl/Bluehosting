package com.blue.hosting.utils.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CookieManagement {
    public static Cookie add(String name, int maxAge, String path, String val){
        Cookie cookie = new Cookie(name, val);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        return cookie;
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

    public static void delete(HttpServletResponse response, String name, Cookie[] cookies){
        Cookie cook = search(name, cookies);
        if(cook == null){
            return;
        }

        cook = new Cookie(name, null);
        cook.setMaxAge(0);
        cook.setPath("/");
        response.addCookie(cook);
    }
}
