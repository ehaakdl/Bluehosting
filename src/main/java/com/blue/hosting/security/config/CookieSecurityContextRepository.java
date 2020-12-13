package com.blue.hosting.security.config;

import antlr.Token;
import com.blue.hosting.entity.token.BlacklistTokenInfoDAO;
import com.blue.hosting.entity.token.BlacklistTokenInfoRepo;
import com.blue.hosting.entity.token.TokenInfoDAO;
import com.blue.hosting.entity.token.TokenInfoRepo;
import com.blue.hosting.security.authentication.account.JwtCertificationToken;
import com.blue.hosting.utils.cookie.CookieManagement;
import com.blue.hosting.utils.cookie.eCookie;
import com.blue.hosting.utils.token.JwtTokenManagement;
import com.blue.hosting.utils.token.TokenAttribute;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.*;

public class CookieSecurityContextRepository implements SecurityContextRepository {
    @Resource(name="jwtTokenManagement")
    public void setmJwtTokenManagement(JwtTokenManagement mJwtTokenManagement) {
        this.mJwtTokenManagement = mJwtTokenManagement;
    }

    private JwtTokenManagement mJwtTokenManagement;

    @Resource(name="tokenInfoRepo")
    public void setmTokenInfoRepo(TokenInfoRepo mTokenInfoRepo) {
        this.mTokenInfoRepo = mTokenInfoRepo;
    }

    private TokenInfoRepo mTokenInfoRepo;

    @Resource(name="blacklistTokenInfoRepo")
    public void setmBlacklistTokenInfoRepo(BlacklistTokenInfoRepo mBlacklistTokenInfoRepo) {
        this.mBlacklistTokenInfoRepo = mBlacklistTokenInfoRepo;
    }

    private BlacklistTokenInfoRepo mBlacklistTokenInfoRepo;


    private boolean isBlacklistToken(String token, String tokenType ,Cookie[] cookies, HttpServletResponse res){
        if(mJwtTokenManagement.isSearchBlackList(token.toString()) == false){
            return false;
        }
        CookieManagement.delete(res, tokenType, cookies);
        return true;
    }

    private boolean isVerify(String token, String tokenType, Cookie[] cookies, HttpServletResponse res){
        if(mJwtTokenManagement.isVerify(token) == false){
            CookieManagement.delete(res, tokenType, cookies);
            return false;
        }
        return true;
    }



    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder httpRequestResponseHolder) {
        HttpServletRequest req = httpRequestResponseHolder.getRequest();
        HttpServletResponse res = httpRequestResponseHolder.getResponse();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Cookie[] cookies = req.getCookies();
        Cookie cook = CookieManagement.search(TokenAttribute.ACCESS_TOKEN, cookies);
        StringBuilder tokenBuilder;

        Map claims;
        //로그인안한 경우
        if(cook == null){
            //access token 재발급
            String token = mJwtTokenManagement.refresh(cookies, res);
            if(token == null){
                return securityContext;
            }
            claims = mJwtTokenManagement.getClaims(token);
        }
        //로그인한 경우
        else if(isBlacklistToken(cook.getValue(), TokenAttribute.ACCESS_TOKEN, req.getCookies(), res)){
            String token = mJwtTokenManagement.refresh(cookies, res);
            if(token == null){
                return securityContext;
            }
            claims = mJwtTokenManagement.getClaims(token);
        } else {
            if(isVerify(cook.getValue(), TokenAttribute.ACCESS_TOKEN ,cookies, res)){
                claims = mJwtTokenManagement.getClaims(cook.getValue());
                if(claims == null){
                    String token = mJwtTokenManagement.refresh(cookies, res);
                    if(token == null){
                        return securityContext;
                    }
                    claims = mJwtTokenManagement.getClaims(token);
                }
            }else{
                CookieManagement.delete(res, TokenAttribute.ACCESS_TOKEN, cookies);
                CookieManagement.delete(res, TokenAttribute.REFRESH_TOKEN, cookies);
                return securityContext;
            }
        }

        JwtCertificationToken authToken = new JwtCertificationToken((String)claims.get(TokenAttribute.ID_CLAIM));
        securityContext.setAuthentication(authToken);
        return securityContext;
    }

    @Override
    public void saveContext(SecurityContext securityContext, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

    }

    @Override
    public boolean containsContext(HttpServletRequest httpServletRequest) {
        return false;
    }
}
