package com.blue.hosting.security.handler.account;

import com.blue.hosting.entity.token.TokenInfoDAO;
import com.blue.hosting.entity.token.TokenInfoRepo;
import com.blue.hosting.utils.HttpStatusCode;
import com.blue.hosting.utils.cookie.CookieManagement;
import com.blue.hosting.utils.cookie.eCookie;
import com.blue.hosting.utils.token.JwtTokenManagement;
import com.blue.hosting.utils.token.TokenAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AccountLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private TokenInfoRepo mTokenInfoRepo;
    private final String JSON_TYPE = "application/json";

    @Resource(name="tokenInfoRepo")
    private void setTokeInfoRepo(TokenInfoRepo tokenInfoRepo){
        mTokenInfoRepo = tokenInfoRepo;
    }

    @Resource(name = "jwtTokenManagement")
    public void setmJwtTokenManagement(JwtTokenManagement mJwtTokenManagement) {
        this.mJwtTokenManagement = mJwtTokenManagement;
    }

    private JwtTokenManagement mJwtTokenManagement;

    private String generateToken(String type, String id){
        String token = null;
        Map claims = new HashMap();
        Map headers = new HashMap();
        Date expireDate = null;
        claims.put(TokenAttribute.ID_CLAIM, id);
        claims.put(TokenAttribute.IAT_CLAIM, System.currentTimeMillis());
        headers.put(TokenAttribute.ALG_HEADER, TokenAttribute.HS256);
        headers.put(TokenAttribute.TYP_HEADER, TokenAttribute.JWT);
        if(type.equals(TokenAttribute.ACCESS_TOKEN)){
            expireDate = mJwtTokenManagement.createExpireDate(TokenAttribute.ACCESS_EXPIRETIME);
        }else if(type.equals(TokenAttribute.REFRESH_TOKEN)){
            expireDate = mJwtTokenManagement.createExpireDate(TokenAttribute.REFRESH_EXPIRETIME);
        }
        token = mJwtTokenManagement.create(expireDate, id, headers, claims);
        return token;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        String id =  (String) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        try {
            String accessToken = generateToken(TokenAttribute.ACCESS_TOKEN, id);
            String refreshToken = generateToken(TokenAttribute.REFRESH_TOKEN, id);
            if(accessToken == null || refreshToken == null){
                response.sendError(HttpStatusCode.TOKEN_CREATE_FAILED);
                //log
                throw new Exception();
            }

            response.setContentType(JSON_TYPE);
            eCookie cookAttr = eCookie.ACCESS_TOKEN;
            Cookie cookie = CookieManagement.add(cookAttr.getName(), cookAttr.getMaxAge(), cookAttr.getPath(), accessToken);
            response.addCookie(cookie);
            cookAttr = eCookie.REFRESH_TOKEN;
            cookie = CookieManagement.add(cookAttr.getName(), cookAttr.getMaxAge(), cookAttr.getPath(), refreshToken);
            response.addCookie(cookie);

            TokenInfoDAO tokenInfoDAO = new TokenInfoDAO(refreshToken, id);
            mTokenInfoRepo.saveAndFlush(tokenInfoDAO);
        } catch(Exception Except) {
            response.reset();

            //log
        }
    }
}
