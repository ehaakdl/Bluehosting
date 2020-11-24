package com.blue.hosting.security.config;

import com.blue.hosting.entity.token.BlacklistTokenInfoDAO;
import com.blue.hosting.entity.token.BlacklistTokenInfoRepo;
import com.blue.hosting.entity.token.TokenInfoDAO;
import com.blue.hosting.entity.token.TokenInfoRepo;
import com.blue.hosting.utils.cookie.CookieManagement;
import com.blue.hosting.utils.token.JwtTokenManagement;
import com.blue.hosting.utils.token.TokenAttribute;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder httpRequestResponseHolder) {
        HttpServletRequest req = httpRequestResponseHolder.getRequest();
        HttpServletResponse res = httpRequestResponseHolder.getResponse();
        /*
        토큰 두개 꺼낸다.
        blacklist 검사
        verify 검사
         */
        Cookie cook = CookieManagement.search(TokenAttribute.ACCESS_TOKEN, req.getCookies());
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if(cook == null){
            return securityContext;
        }

        StringBuilder token = new StringBuilder(cook.getValue());
        if(mJwtTokenManagement.isSearchBlackList(token.toString())){
            cook = CookieManagement.search(TokenAttribute.REFRESH_TOKEN, req.getCookies());
            token.delete(0, token.length());
            token.append(cook.getValue());
            if(mJwtTokenManagement.verify(token.toString())){
                mTokenInfoRepo.deleteById(token.toString());
            }
            return securityContext;
        }

        token.delete(0, token.length());
        cook = CookieManagement.search(TokenAttribute.ACCESS_TOKEN, req.getCookies());
        if(cook == null){
            return securityContext;
        }
        token.append(cook.getValue());
        if(mJwtTokenManagement.verify(token.toString()) == false){
            mJwtTokenManagement.delete(token.toString(), null, TokenAttribute.ACCESS_TOKEN);
            cook = CookieManagement.search(TokenAttribute.REFRESH_TOKEN, req.getCookies());
            token.delete(0, token.length());
            token.append(cook.getValue());
            if(mJwtTokenManagement.verify(token.toString())){
                mTokenInfoRepo.deleteById(token.toString());
            }
        }


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
