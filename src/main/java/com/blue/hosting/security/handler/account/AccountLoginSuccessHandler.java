package com.blue.hosting.security.handler.account;

import com.blue.hosting.entity.token.TokenInfoDAO;
import com.blue.hosting.entity.token.TokenInfoRepo;
import com.blue.hosting.security.exception.eSystemException;
import com.blue.hosting.utils.ResponseStatusCode;
import com.blue.hosting.utils.cookie.CookieManagement;
import com.blue.hosting.utils.cookie.eCookie;
import com.blue.hosting.utils.token.JwtTokenManagement;
import com.blue.hosting.utils.token.TokenAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AccountLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private TokenInfoRepo mTokenInfoRepo;
    private final String JSON_TYPE = "application/json";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name="tokenInfoRepo")
    private void setTokeInfoRepo(TokenInfoRepo tokenInfoRepo){
        mTokenInfoRepo = tokenInfoRepo;
    }

    @Resource(name = "jwtTokenManagement")
    public void setmJwtTokenManagement(JwtTokenManagement mJwtTokenManagement) {
        this.mJwtTokenManagement = mJwtTokenManagement;
    }

    private JwtTokenManagement mJwtTokenManagement;

    private Map generateToken(String type, String id){
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
        token = mJwtTokenManagement.create(expireDate, headers, claims);
        Map resultMap = new HashMap();
        resultMap.put(TOKEN_CLAIM_NAME, token);
        resultMap.put(DATE_CLAIM_NAME, expireDate);
        return resultMap;
    }
    private final String TOKEN_CLAIM_NAME = "TOKEN_CLAIM";

    private final String DATE_CLAIM_NAME = "DATE_CLAIM";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        String id =  (String) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Map tokenMap = generateToken(TokenAttribute.ACCESS_TOKEN, id);
        String accessToken = (String) tokenMap.get(TOKEN_CLAIM_NAME);
        tokenMap = generateToken(TokenAttribute.REFRESH_TOKEN, id);
        String refreshToken = (String) tokenMap.get(TOKEN_CLAIM_NAME);
        Date refreshTokenExpireTime = (Date) tokenMap.get(DATE_CLAIM_NAME);
        if(accessToken == null || refreshToken == null){
            try {
                response.sendError(ResponseStatusCode.TOKEN_CREATE_FAILED);
            } catch (IOException e) {
                StackTraceElement[] stackArray = e.getStackTrace();
                for (StackTraceElement stackTraceElement : stackArray) {
                    String className = "class name:" +
                            stackTraceElement.getClassName() + '\n';
                    String methodName = "method name:" +
                            '[' +
                            stackTraceElement.getLineNumber() +
                            ']' +
                            stackTraceElement.getMethodName() + '\n';
                    String fileName = "file name:" + stackTraceElement.getFileName() + '\n';
                    StringBuilder builder = new StringBuilder(className);
                    builder.lastIndexOf(methodName);
                    builder.lastIndexOf(fileName);
                    logger.debug(builder.toString());
                }
            }
            logger.debug(eSystemException.CREATE_FAIL_TOKEN.getMsg());
            return;
        }
        try {
            TokenInfoDAO tokenInfoDAO = new TokenInfoDAO(refreshToken, id, refreshTokenExpireTime.getTime());
            if(insertTokenInfo(tokenInfoDAO).equals(tokenInfoDAO) == false){
                throw new RuntimeException();
            }
            response.setContentType(JSON_TYPE);
            eCookie cookAttr = eCookie.ACCESS_TOKEN;
            Cookie cookie = CookieManagement.add(cookAttr.getName(), cookAttr.getMaxAge(), cookAttr.getPath(), accessToken);
            response.addCookie(cookie);
            cookAttr = eCookie.REFRESH_TOKEN;
            cookie = CookieManagement.add(cookAttr.getName(), cookAttr.getMaxAge(), cookAttr.getPath(), refreshToken);
            response.addCookie(cookie);
            response.setStatus(ResponseStatusCode.SUCCESS_LOGIN);
        } catch(Exception e) {
            response.setStatus(ResponseStatusCode.FAIL_LOGIN);
            logger.debug(eSystemException.INSERT_FAIL_TOKEN_INFO_TABLE.getMsg());
        }
    }

    @Transactional
    protected TokenInfoDAO insertTokenInfo(TokenInfoDAO tokenInfoDAO){
        return mTokenInfoRepo.save(tokenInfoDAO);
    }
}
