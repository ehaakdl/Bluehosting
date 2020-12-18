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
    @Resource(name = "jwtTokenManagement")
    public void setmJwtTokenManagement(JwtTokenManagement mJwtTokenManagement) {
        this.mJwtTokenManagement = mJwtTokenManagement;
    }

    private JwtTokenManagement mJwtTokenManagement;

    @Resource(name = "tokenInfoRepo")
    public void setmTokenInfoRepo(TokenInfoRepo mTokenInfoRepo) {
        this.mTokenInfoRepo = mTokenInfoRepo;
    }

    private TokenInfoRepo mTokenInfoRepo;

    @Resource(name = "blacklistTokenInfoRepo")
    public void setmBlacklistTokenInfoRepo(BlacklistTokenInfoRepo mBlacklistTokenInfoRepo) {
        this.mBlacklistTokenInfoRepo = mBlacklistTokenInfoRepo;
    }

    private BlacklistTokenInfoRepo mBlacklistTokenInfoRepo;


    private boolean isBlacklistToken(String token, Cookie[] cookies, HttpServletResponse res) {
        if (mJwtTokenManagement.isBlackList(token.toString()) == false) {
            return false;
        }
        Cookie cook = CookieManagement.search(TokenAttribute.ACCESS_TOKEN, cookies);
        String accessToken = null;
        if (cook != null) {
            accessToken = cook.getValue();
        }
        cook = CookieManagement.search(TokenAttribute.REFRESH_TOKEN, cookies);
        String refreshToken = null;
        if (cook != null) {
            refreshToken = cook.getValue();
        }
        try {
            mJwtTokenManagement.deleteAllTokenDB(accessToken, refreshToken);
        } catch (Exception e) {
            //Rollback log
            CookieManagement.delete(res, TokenAttribute.ACCESS_TOKEN, cookies);
            CookieManagement.delete(res, TokenAttribute.REFRESH_TOKEN, cookies);
        }
        return true;
    }

    private boolean isVerify(String token, Cookie[] cookies, HttpServletResponse res) {
        if (mJwtTokenManagement.isVerify(token)) {
            return true;
        }

        Cookie cook = CookieManagement.search(TokenAttribute.ACCESS_TOKEN, cookies);
        String accessToken = null;
        if (cook != null) {
            accessToken = cook.getValue();
        }
        cook = CookieManagement.search(TokenAttribute.REFRESH_TOKEN, cookies);
        String refreshToken = null;
        if (cook != null) {
            refreshToken = cook.getValue();
        }
        try {
            mJwtTokenManagement.deleteAllTokenDB(accessToken, refreshToken);
        } catch (Exception e) {
            //Rollback log
            CookieManagement.delete(res, TokenAttribute.ACCESS_TOKEN, cookies);
            CookieManagement.delete(res, TokenAttribute.REFRESH_TOKEN, cookies);
        }
        return false;
    }

    private Map refresh(Cookie[] cookies, HttpServletResponse res) {
        String token = mJwtTokenManagement.refresh(cookies, res);
        if (token != null) {
            return mJwtTokenManagement.getClaims(token);
        }

        Cookie cook = CookieManagement.search(TokenAttribute.ACCESS_TOKEN, cookies);
        String accessToken = null;
        if (cook != null) {
            accessToken = cook.getValue();
        }
        cook = CookieManagement.search(TokenAttribute.REFRESH_TOKEN, cookies);
        String refreshToken = null;
        if (cook != null) {
            refreshToken = cook.getValue();
        }
        try {
            mJwtTokenManagement.deleteAllTokenDB(accessToken, refreshToken);
        } catch (Exception e) {
            //Rollback log
            CookieManagement.delete(res, TokenAttribute.ACCESS_TOKEN, cookies);
            CookieManagement.delete(res, TokenAttribute.REFRESH_TOKEN, cookies);
        }
        return null;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder httpRequestResponseHolder) {
        HttpServletRequest req = httpRequestResponseHolder.getRequest();
        HttpServletResponse res = httpRequestResponseHolder.getResponse();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Cookie[] cookies = req.getCookies();
        Cookie cook = CookieManagement.search(TokenAttribute.ACCESS_TOKEN, cookies);
        Map claims = null;
        if (cook == null) {
            if (mJwtTokenManagement.isAvailRefresh(cookies) == false) {
                return securityContext;
            }
            String token = mJwtTokenManagement.refresh(cookies, res);
            if (token == null) {
                return securityContext;
            }
            claims = mJwtTokenManagement.getClaims(token);
        } else if (isBlacklistToken(cook.getValue(), cookies, res)) {
            return securityContext;
        } else {
            if (isVerify(cook.getValue(), cookies, res)) {
                claims = mJwtTokenManagement.getClaims(cook.getValue());
                if (claims == null) {
                    claims = refresh(cookies, res);
                    if (claims == null) {
                        return securityContext;
                    }
                }
            } else {
                String accessToken = cook.getValue();
                cook = CookieManagement.search(TokenAttribute.REFRESH_TOKEN, cookies);
                String refreshToken = cook.getValue();
                try {
                    mJwtTokenManagement.deleteAllTokenDB(accessToken, refreshToken);
                } catch (Exception e) {
                    //Rollback log
                    CookieManagement.delete(res, TokenAttribute.ACCESS_TOKEN, cookies);
                    CookieManagement.delete(res, TokenAttribute.REFRESH_TOKEN, cookies);
                }
                return securityContext;
            }
        }

        JwtCertificationToken authToken = new JwtCertificationToken((String) claims.get(TokenAttribute.ID_CLAIM));
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
