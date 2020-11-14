package com.blue.hosting.security;

import com.blue.hosting.entity.token.BlacklistTokenInfoDAO;
import com.blue.hosting.entity.token.BlacklistTokenInfoRepo;
import com.blue.hosting.utils.cookie.CookieMangement;
import com.blue.hosting.utils.token.ClientTokenMange;
import com.blue.hosting.utils.token.JwtTokenHelper;
import com.blue.hosting.utils.token.eTokenVal;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class CustomSecurityContextRepository implements SecurityContextRepository {
    @Resource(name="clientTokenMange")
    public void setmClientTokenMange(ClientTokenMange mClientTokenMange) {
        this.mClientTokenMange = mClientTokenMange;
    }

    private ClientTokenMange mClientTokenMange;

    @Resource(name="blacklistTokenInfoRepo")
    public void setmBlacklistTokenInfoRepo(BlacklistTokenInfoRepo mBlacklistTokenInfoRepo) {
        this.mBlacklistTokenInfoRepo = mBlacklistTokenInfoRepo;
    }

    private BlacklistTokenInfoRepo mBlacklistTokenInfoRepo;


    private Map<String, Object> verifyToken(eTokenVal tokenVal, String hash){
        Map<String, Object> claimMap = null;
        try {
            claimMap = JwtTokenHelper.verifyToken(tokenVal, hash);
            if(claimMap == null){
                return null;
            }
        }catch (ExpiredJwtException except){
            return claimMap;
        }
        return claimMap;
    }


    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder httpRequestResponseHolder) {
        HttpServletRequest req = httpRequestResponseHolder.getRequest();
        HttpServletResponse res = httpRequestResponseHolder.getResponse();
        eTokenVal tokenVal = eTokenVal.ACCESS_TOKEN;
        Cookie cook = CookieMangement.search(tokenVal.getmTokenType(), req.getCookies());
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if(cook == null){
            return securityContext;
        }

        if(mClientTokenMange.isSearchBlacklist(cook.getValue())){
            return securityContext;
        }

        String token = cook.getValue();
        Map<String, Object> claimMap = null;
        try{
            claimMap = JwtTokenHelper.verifyToken(tokenVal, token);
            if(claimMap == null){
                cook.setMaxAge(0);
                res.addCookie(cook);
                BlacklistTokenInfoDAO blacklistTokenInfoDAO = new BlacklistTokenInfoDAO(token);
                mBlacklistTokenInfoRepo.saveAndFlush(blacklistTokenInfoDAO);
                return securityContext;
            }
        }catch (ExpiredJwtException except){
            mClientTokenMange.refresh(req,res);
        }

        if(claimMap == null){
            return securityContext;
        }
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(claimMap.get(tokenVal.getmIdClaimNm())
        , null, null);
        securityContext.setAuthentication(usernamePasswordAuthenticationToken);
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
