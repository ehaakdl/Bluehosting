package com.blue.hosting.security.login;

import com.blue.hosting.entity.token.TokenInfoDAO;
import com.blue.hosting.entity.token.TokenInfoRepo;
import com.blue.hosting.utils.token.eTokenVal;
import com.blue.hosting.utils.token.JwtTokenHelper;
import com.blue.hosting.utils.cookie.eCookie;
import com.blue.hosting.utils.eHttpHeader;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private TokenInfoRepo mTokenInfoRepo;

    @Resource(name="tokenInfoRepo")
    private void setTokeInfoRepo(TokenInfoRepo tokenInfoRepo){
        mTokenInfoRepo = tokenInfoRepo;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        String accountId =  (String) authentication.getPrincipal();

        /*
        context를 가져와서 인증을 context에 넣어준다.
         */
        //SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            JwtTokenHelper jwtTokenHelper = new JwtTokenHelper();
            String accessToken = jwtTokenHelper.generate(accountId, eTokenVal.ACCESS_TOKEN);
            String refreshToken = jwtTokenHelper.generate(accountId, eTokenVal.REFRESH_TOKEN);

            eHttpHeader httpHeader = eHttpHeader.JSON_TYPE;
            response.setContentType(httpHeader.getHeader());

            eCookie defCookVal = eCookie.ACCESS_TOKEN;
            Cookie cookie = new Cookie(defCookVal.getName(), accessToken);
            cookie.setMaxAge(defCookVal.getMaxAge());
            cookie.setPath(defCookVal.getPath());
            response.addCookie(cookie);

            defCookVal = eCookie.REFRESH_TOKEN;
            cookie = new Cookie(defCookVal.getName(), refreshToken);
            cookie.setMaxAge(defCookVal.getMaxAge());
            cookie.setPath(defCookVal.getPath());
            response.addCookie(cookie);
            TokenInfoDAO tokenInfoDAO = new TokenInfoDAO(refreshToken, accountId);
            mTokenInfoRepo.save(tokenInfoDAO);
        } catch(Exception Except) {
            response.reset();
            //log
        }
    }
}
