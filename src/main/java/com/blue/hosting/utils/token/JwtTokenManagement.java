package com.blue.hosting.utils.token;

import com.blue.hosting.entity.token.BlacklistTokenInfoDAO;
import com.blue.hosting.entity.token.BlacklistTokenInfoRepo;
import com.blue.hosting.entity.token.TokenInfoDAO;
import com.blue.hosting.entity.token.TokenInfoRepo;
import com.blue.hosting.security.exception.eSystemException;
import com.blue.hosting.utils.cookie.CookieManagement;
import com.blue.hosting.utils.cookie.eCookie;
import io.jsonwebtoken.*;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.*;

@Component("jwtTokenManagement")
public class JwtTokenManagement {
    @Resource(name="tokenInfoRepo")
    public void setmTokenInfoRepo(TokenInfoRepo mTokenInfoRepo) {
        this.mTokenInfoRepo = mTokenInfoRepo;
    }
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private TokenInfoRepo mTokenInfoRepo;

    @Transactional
    public void delete(String token, String id, String tokenType) {
        BlacklistTokenInfoDAO blacklistTokenInfoDAO = new BlacklistTokenInfoDAO(token);
        mBlacklistTokenInfoRepo.save(blacklistTokenInfoDAO);
        if (tokenType.equals(TokenAttribute.REFRESH_TOKEN)) {
            mTokenInfoRepo.deleteById(id);
        }
    }

    @Resource(name = "blacklistTokenInfoRepo")
    public void setmBlacklistTokenInfoRepo(BlacklistTokenInfoRepo mBlacklistTokenInfoRepo) {
        this.mBlacklistTokenInfoRepo = mBlacklistTokenInfoRepo;
    }

    private BlacklistTokenInfoRepo mBlacklistTokenInfoRepo;


    public boolean isBlackList(String token) {
        Optional<BlacklistTokenInfoDAO> optional = mBlacklistTokenInfoRepo.findById(token);
        try {
            BlacklistTokenInfoDAO blacklistTokenInfoDAO = optional.get();
        } catch (NoSuchElementException e) {
            return false;
        }

        return true;
    }

    private Key createSigningKey() {
        String secretKey = TokenAttribute.SECRETKEY;
        byte[] secretKeyBytes;
        try {
            secretKeyBytes = secretKey.getBytes(TokenAttribute.CHARSET);
        } catch (UnsupportedEncodingException except) {
            return null;
        }
        String jcaName = SignatureAlgorithm.HS256.getJcaName();
        return new SecretKeySpec(secretKeyBytes, jcaName);
    }

    public Date createExpireDate(long expireTime) {
        Date date = new Date();
        long time = date.getTime() + expireTime;
        date.setTime(time);
        return date;
    }


    public String create(Date expireDate, Map<String, Object> header, Map<String, Object> claim) {
        Key key = createSigningKey();
        if (key == null) {
            return null;
        }
        JwtBuilder builder = Jwts.builder()
                .setHeader(header)
                .setClaims(claim)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, key);
        String token = builder.compact();
        return token;
    }

    public boolean isVerify(String token) {
        String secretKey = TokenAttribute.SECRETKEY;
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes(TokenAttribute.CHARSET))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException except) {
            return true;
        } catch (Exception except) {
            return false;
        }
        return true;
    }

    public Map getClaims(String token) {
        String secretKey = TokenAttribute.SECRETKEY;
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes(TokenAttribute.CHARSET))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException except) {
            return null;
        } catch (Exception except) {
            return null;
        }
        return claims;
    }
    private TokenInfoDAO getTokenInfo(@NonNull String token){
        TokenInfoDAO tokenInfoDAO = new TokenInfoDAO(token, null);
        Optional<TokenInfoDAO> optional = mTokenInfoRepo.findById(token);
        try{
            optional.get();
        }catch (NoSuchElementException except){
            return null;
        }
        return optional.get();
    }

    public boolean isAvailRefresh(Cookie[] cookies) {
        Cookie cook = CookieManagement.search(TokenAttribute.REFRESH_TOKEN, cookies);
        if(cook == null){
            return false;
        }
        String refreshToken = cook.getValue();
        if(isBlackList(refreshToken)){
            return false;
        }
        if(isVerify(refreshToken) == false){
            return false;
        }
        return true;
    }

    private boolean insertBlackList(String token){
        BlacklistTokenInfoDAO insertDAO = new BlacklistTokenInfoDAO(token);
        BlacklistTokenInfoDAO resultDAO;
        try {
            resultDAO = mBlacklistTokenInfoRepo.save(insertDAO);
        }catch (IllegalArgumentException e){
            return false;
        }
        if(resultDAO.equals(insertDAO) == false){
            return false;
        }
        return true;
    }

    private boolean deleteByIdTokenInfo(String token){
        try {
            mTokenInfoRepo.deleteById(token);
        }catch (IllegalArgumentException e){
            return false;
        }
        return true;
    }

    @Transactional
    public void deleteAllTokenDB(String accessToken, String refreshToken) throws Exception{
        if(accessToken != null){
            if(insertBlackList(accessToken) == false){
                throw new RuntimeException();
            }
        }
        if(refreshToken != null){
            if(insertBlackList(refreshToken) == false){
                throw new RuntimeException();
            }
            if(deleteByIdTokenInfo(refreshToken) == false){
                throw new RuntimeException();
            }
        }
    }

    public String refresh(Cookie[] cookies, HttpServletResponse res) {
        Cookie cook = CookieManagement.search(TokenAttribute.ACCESS_TOKEN, cookies);
        StringBuilder accessToken = null;
        if(cook != null){
            accessToken = new StringBuilder(cook.getValue());
        }
        cook = CookieManagement.search(TokenAttribute.REFRESH_TOKEN, cookies);
        StringBuilder refreshToken = null;
        if(cook != null){
            refreshToken = new StringBuilder(cook.getValue());
        }

        if(isAvailRefresh(cookies) == false){
            try{
                deleteAllTokenDB(accessToken.toString(), refreshToken.toString());
            } catch (Exception e){
                //Rollback log 남기기
            }
            CookieManagement.delete(res, TokenAttribute.ACCESS_TOKEN, cookies);
            CookieManagement.delete(res, TokenAttribute.REFRESH_TOKEN, cookies);
            return null;
        }

        Date expireDate = null;
        Map claims = getClaims(refreshToken.toString());
        Map headers = null;
        Map paramClaims = null;
        eCookie cookAttr;
        if(claims == null){
            TokenInfoDAO tokenInfoDAO = getTokenInfo(refreshToken.toString());
            if(tokenInfoDAO == null){
                try{
                    deleteAllTokenDB(accessToken.toString(), refreshToken.toString());
                } catch (Exception e){
                    //Rollback 어떤 token인지 log 남기기
                }
                CookieManagement.delete(res, TokenAttribute.ACCESS_TOKEN, cookies);
                CookieManagement.delete(res, TokenAttribute.REFRESH_TOKEN, cookies);
                return null;
            }

            headers = setHeader();
            paramClaims = setCliam(tokenInfoDAO.getmUsername());
            refreshToken.delete(0, refreshToken.length());
            expireDate = expireDate = createExpireDate(TokenAttribute.REFRESH_EXPIRETIME);
            refreshToken.append(create(expireDate, headers, paramClaims));
            if(refreshToken.toString() == null){
                try{
                    deleteAllTokenDB(accessToken.toString(), refreshToken.toString());
                } catch (Exception e){
                    String errMsg = eSystemException.DELETE_ALL_TOKEN_FAIL.getMsg()+
                            '\n' + "accessToken:" + accessToken + '\n' + "refreshToken:" + refreshToken
                            + '\n' + e.getStackTrace();
                    logger.debug(errMsg);
                }
                CookieManagement.delete(res, TokenAttribute.ACCESS_TOKEN, cookies);
                CookieManagement.delete(res, TokenAttribute.REFRESH_TOKEN, cookies);
                return null;
            }
            try {
                updateTokenInfo(refreshToken.toString(), tokenInfoDAO.getmJwtHash(), tokenInfoDAO.getmUsername());
            }catch (Exception e){
                try{
                    deleteAllTokenDB(accessToken.toString(), tokenInfoDAO.getmJwtHash());
                } catch (Exception except){
                    String errMsg = eSystemException.DELETE_ALL_TOKEN_FAIL.getMsg()+
                            '\n' + "accessToken:" + accessToken + '\n' + "refreshToken:" + refreshToken
                            + '\n' + e.getStackTrace();
                    logger.debug(errMsg);
                }
                CookieManagement.delete(res, TokenAttribute.ACCESS_TOKEN, cookies);
                CookieManagement.delete(res, TokenAttribute.REFRESH_TOKEN, cookies);
                return null;
            }
            cookAttr = eCookie.REFRESH_TOKEN;
            cook = CookieManagement.add(TokenAttribute.REFRESH_TOKEN, cookAttr.getMaxAge(), cookAttr.getPath(), refreshToken.toString());
            res.addCookie(cook);
            claims = getClaims(refreshToken.toString());
            if(claims == null){
                return null;
            }
        }

        headers = setHeader();
        expireDate = createExpireDate(TokenAttribute.ACCESS_EXPIRETIME);
        paramClaims = setCliam((String)claims.get(TokenAttribute.ID_CLAIM));
        if(accessToken == null) {
            accessToken = new StringBuilder();
        }
        accessToken.delete(0, accessToken.length());
        accessToken.append(create(expireDate, headers, paramClaims));
        if(accessToken.toString() == null){
            String errMsg = eSystemException.CREATE_FAIL_TOKEN.getMsg();
            logger.debug(errMsg);
            try{
                deleteAllTokenDB(accessToken.toString(), refreshToken.toString());
            } catch (Exception except){
                errMsg = eSystemException.DELETE_ALL_TOKEN_FAIL.getMsg()+
                        '\n' + "accessToken:" + accessToken + '\n' + "refreshToken:" + refreshToken
                        + '\n' + except.getStackTrace();
                logger.debug(errMsg);
            }
            CookieManagement.delete(res, TokenAttribute.ACCESS_TOKEN, cookies);
            CookieManagement.delete(res, TokenAttribute.REFRESH_TOKEN, cookies);
            return null;
        }
        cookAttr = eCookie.ACCESS_TOKEN;
        cook = CookieManagement.add(TokenAttribute.ACCESS_TOKEN, cookAttr.getMaxAge(), cookAttr.getPath(), accessToken.toString());
        res.addCookie(cook);
        return accessToken.toString();
    }

    @Transactional
    protected void updateTokenInfo(String dst, String src, String id) throws Exception{
        if(insertBlackList(src) == false){
            throw new RuntimeException();
        }
        if(deleteByIdTokenInfo(src) == false){
            throw new RuntimeException();
        }
        if(insertTokenInfo(dst, id) == false){
            throw new RuntimeException();
        }
    }
    private boolean insertTokenInfo(String token, String id){
        TokenInfoDAO insertDAO = new TokenInfoDAO(token, id);
        TokenInfoDAO resultDAO = mTokenInfoRepo.save(insertDAO);
        if(resultDAO.equals(insertDAO) == false){
            return false;
        }
        return true;
    }
    private Map setCliam(String id){
        Map claims = new HashMap();
        claims.put(TokenAttribute.ID_CLAIM, id);
        claims.put(TokenAttribute.IAT_CLAIM, System.currentTimeMillis());
        return claims;
    }
    private Map setHeader(){
        Map headers = new HashMap();
        headers.put(TokenAttribute.ALG_HEADER, TokenAttribute.HS256);
        headers.put(TokenAttribute.TYP_HEADER, TokenAttribute.JWT);
        return headers;
    }

}

