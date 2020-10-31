package com.blue.hosting.services.account.login;

import com.blue.hosting.entity.TokenInfoDTO;
import com.blue.hosting.entity.TokenInfoRepo;
import com.blue.hosting.services.account.eTokenField;
import com.blue.hosting.utils.token.JwtTokenHelper;
import com.blue.hosting.utils.eCookie;
import com.blue.hosting.utils.eHttpHeader;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private JwtTokenHelper mJwtTokenHelper;
    private TokenInfoRepo mTokenInfoRepo;

    @Resource(name="jwtTokenHelper")
    private void setJwtTokenHelper(JwtTokenHelper jwtTokenHelper){
        mJwtTokenHelper = jwtTokenHelper;
    }

    @Resource(name="tokeInfoRepo")
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

            String accessToken = mJwtTokenHelper.generate(accountId, eTokenField.ACCESS_TOKEN);
            String refreshToken = mJwtTokenHelper.generate(accountId, eTokenField.REFRESH_TOKEN);
            //Refresh token 디비에 해쉬값 저장하기

            TokenInfoDTO tokenInfoDTO = new TokenInfoDTO(refreshToken, accountId);
            mTokenInfoRepo.save(tokenInfoDTO);

            eHttpHeader httpHeader = eHttpHeader.JSON_TYPE;
            response.setContentType(httpHeader.getHeader());

            eCookie defCookVal = eCookie.ACCESS_TOKEN;
            Cookie cookie = new Cookie(defCookVal.getName(), accessToken);
            cookie.setMaxAge(defCookVal.getMaxAge());
            cookie.setDomain(defCookVal.getDomain());
            response.addCookie(cookie);

            defCookVal = eCookie.REFRESH_TOKEN;
            cookie.setMaxAge(defCookVal.getMaxAge());
            cookie.setDomain(defCookVal.getDomain());
            cookie = new Cookie(defCookVal.getName(), refreshToken);
            response.addCookie(cookie);
        } catch(Exception Except) {
            response.reset();
            //log
        }
    }
}
