package com.blue.hosting.security.handler.account;

import com.blue.hosting.entity.token.BlacklistTokenInfoDAO;
import com.blue.hosting.entity.token.BlacklistTokenInfoRepo;
import com.blue.hosting.entity.token.TokenInfoDAO;
import com.blue.hosting.entity.token.TokenInfoRepo;
import com.blue.hosting.utils.cookie.CookieManagement;
import com.blue.hosting.utils.token.TokenAttribute;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

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

    private void deleteToken(Cookie cookie){
        BlacklistTokenInfoDAO blacklistTokenInfoDAO = new BlacklistTokenInfoDAO(cookie.getValue());
        mBlacklistTokenInfoRepo.saveAndFlush();
    }

    private TokenInfoRepo mTokenInfoRepo;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        //로그인이 됐는지 확인한다.
        //디비에서 refreshtoken 지운다.
        //쿠키 access, refresh 지운다.
        Cookie[] cookies = request.getCookies();
        String[] names = {TokenAttribute.ACCESS_TOKEN, TokenAttribute.REFRESH_TOKEN};
        for (String name : names) {
            Cookie cookie = CookieManagement.search(name, cookies);
            if(cookie == null){
                continue;
            }
            deleteToken(cookie);
        }
    }


}
