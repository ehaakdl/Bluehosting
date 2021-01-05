package com.blue.hosting.security.handler.account;

import com.blue.hosting.entity.token.BlacklistTokenInfoDAO;
import com.blue.hosting.entity.token.BlacklistTokenInfoRepo;
import com.blue.hosting.entity.token.TokenInfoDAO;
import com.blue.hosting.entity.token.TokenInfoRepo;
import com.blue.hosting.security.authentication.account.JwtCertificationToken;
import com.blue.hosting.utils.PageIndex;
import com.blue.hosting.utils.cookie.CookieManagement;
import com.blue.hosting.utils.token.JwtTokenManagement;
import com.blue.hosting.utils.token.TokenAttribute;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
public class AccountLogoutHandler implements LogoutHandler {
    @Resource(name="blacklistTokenInfoRepo")
    public void setmBlacklistTokenInfoTb(BlacklistTokenInfoRepo mBlacklistTokenInfoRepo) {
        this.mBlacklistTokenInfoRepo = mBlacklistTokenInfoRepo;
    }

    private BlacklistTokenInfoRepo mBlacklistTokenInfoRepo;

    @Resource(name="tokenInfoRepo")
    public void setmTokenInfoRepo(TokenInfoRepo mTokenInfoRepo) {
        this.mTokenInfoRepo = mTokenInfoRepo;
    }

    private TokenInfoRepo mTokenInfoRepo;

    @Resource(name="jwtTokenManagement")
    public void setmJwtTokenManagement(JwtTokenManagement mJwtTokenManagement) {
        this.mJwtTokenManagement = mJwtTokenManagement;
    }

    private JwtTokenManagement mJwtTokenManagement;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        JwtCertificationToken token = (JwtCertificationToken) SecurityContextHolder.getContext().getAuthentication();
        if(token == null){
            return;
        }

        Cookie[] cookies = request.getCookies();
        String id = (String) token.getPrincipal();
        String[] tokenNames = {TokenAttribute.ACCESS_TOKEN, TokenAttribute.REFRESH_TOKEN};
        for (String tokenName : tokenNames) {
            Cookie cookie = CookieManagement.search(tokenName, cookies);
            if(cookie == null){
                continue;
            }
            if(mJwtTokenManagement.delete(cookie.getValue(), id, tokenName) == false){
                log.error("토큰 삭제 안됨");
            }
            CookieManagement.delete(response, tokenName, cookies);
        }
        try {
            response.sendRedirect(PageIndex.REDIRECT_INDEX);
        }catch (Exception e){
            log.debug("리다이렉트 실패");
        }
    }
}
