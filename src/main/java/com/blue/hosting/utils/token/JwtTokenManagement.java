package com.blue.hosting.utils.token;

import com.blue.hosting.entity.token.BlacklistTokenInfoDAO;
import com.blue.hosting.entity.token.BlacklistTokenInfoRepo;
import com.blue.hosting.entity.token.TokenInfoDAO;
import com.blue.hosting.entity.token.TokenInfoRepo;
import com.blue.hosting.utils.cookie.CookieManagement;
import com.blue.hosting.utils.cookie.eCookie;
import io.jsonwebtoken.*;
import lombok.NonNull;
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


    public boolean isSearchBlackList(String token) {
        Optional<BlacklistTokenInfoDAO> optionalRepo = mBlacklistTokenInfoRepo.findById(token);
        try {
            BlacklistTokenInfoDAO blacklistTokenInfoDAO = optionalRepo.get();
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

    @Transactional
    protected void insertBlackListToken(String token){
        BlacklistTokenInfoDAO blacklistTokenInfoDAO = new BlacklistTokenInfoDAO(token);
        mBlacklistTokenInfoRepo.save(blacklistTokenInfoDAO);
        mTokenInfoRepo.deleteById(token);
    }

    public String refresh(Cookie[] cookies, HttpServletResponse res) {
        Cookie cook = CookieManagement.search(TokenAttribute.REFRESH_TOKEN, cookies);
        if(cook == null){
            return null;
        }
        StringBuilder tokenBuilder = new StringBuilder(cook.getValue());

        //blacklist 확인
        if(isSearchBlackList(tokenBuilder.toString())){
            return null;
        }

        //verify 검사
        if(isVerify(tokenBuilder.toString()) == false){
            return null;
        }

        Date expireDate = null;
        Map claims = getClaims(tokenBuilder.toString());
        Map headers = null;
        Map paramClaims = null;
        eCookie cookAttr;
        //refresh token이 만료됐을떄
        if(claims == null){
            TokenInfoDAO tokenInfoDAO = getTokenInfo(tokenBuilder.toString());
            if(tokenInfoDAO == null){
                BlacklistTokenInfoDAO blacklistTokenInfoDAO = new BlacklistTokenInfoDAO(tokenBuilder.toString());
                mBlacklistTokenInfoRepo.save(blacklistTokenInfoDAO);
                return null;
            }

            headers = setHeader();
            paramClaims = setCliam(tokenInfoDAO.getmUsername());
            tokenBuilder.delete(0, tokenBuilder.length());
            expireDate = expireDate = createExpireDate(TokenAttribute.REFRESH_EXPIRETIME);
            tokenBuilder.append(create(expireDate, headers, paramClaims));
            if(tokenBuilder.toString() == null){
                insertBlackListToken(cook.getValue());
                return null;
            }

            cookAttr = eCookie.REFRESH_TOKEN;
            cook = CookieManagement.add(TokenAttribute.REFRESH_TOKEN, cookAttr.getMaxAge(), cookAttr.getPath(), tokenBuilder.toString());
            res.addCookie(cook);
            claims = getClaims(tokenBuilder.toString());
        }

        headers = setHeader();
        expireDate = createExpireDate(TokenAttribute.ACCESS_EXPIRETIME);
        paramClaims = setCliam((String)claims.get(TokenAttribute.ID_CLAIM));
        tokenBuilder.delete(0, tokenBuilder.length());
        tokenBuilder.append(create(expireDate, headers, paramClaims));
        if(tokenBuilder.toString() == null){
            insertBlackListToken(cook.getValue());
            return null;
        }
        cookAttr = eCookie.ACCESS_TOKEN;
        cook = CookieManagement.add(TokenAttribute.ACCESS_TOKEN, cookAttr.getMaxAge(), cookAttr.getPath(), tokenBuilder.toString());
        res.addCookie(cook);
        return tokenBuilder.toString();
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

