package com.blue.hosting.utils.token;

import com.blue.hosting.entity.token.BlacklistTokenInfoDAO;
import com.blue.hosting.entity.token.BlacklistTokenInfoRepo;
import com.blue.hosting.entity.token.TokenInfoDAO;
import com.blue.hosting.entity.token.TokenInfoRepo;
import com.blue.hosting.utils.cookie.CookieManagement;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.*;

@Component("jwtTokenManagement")
public class JwtTokenManagement {
    private TokenInfoRepo mTokenInfoRepo;

    @Transactional
    public void delete(String token, String id, String tokenType){
        BlacklistTokenInfoDAO blacklistTokenInfoDAO = new BlacklistTokenInfoDAO(token);
        mBlacklistTokenInfoRepo.save(blacklistTokenInfoDAO);
        if(tokenType.equals(TokenAttribute.REFRESH_TOKEN)){
            mTokenInfoRepo.deleteById(id);
        }
    }

    @Resource(name="blacklistTokenInfoRepo")
    public void setmBlacklistTokenInfoRepo(BlacklistTokenInfoRepo mBlacklistTokenInfoRepo) {
        this.mBlacklistTokenInfoRepo = mBlacklistTokenInfoRepo;
    }

    private BlacklistTokenInfoRepo mBlacklistTokenInfoRepo;


    public boolean isSearchBlackList(String token){
        Optional<BlacklistTokenInfoDAO> optionalRepo = mBlacklistTokenInfoRepo.findById(token);
        try {
            BlacklistTokenInfoDAO blacklistTokenInfoDAO = optionalRepo.get();
        }catch (NoSuchElementException e){
            return false;
        }

        return true;
    }

    private Key createSigningKey() {
        String secretKey = TokenAttribute.SECRETKEY;
        byte[] secretKeyBytes;
        try {
            secretKeyBytes = secretKey.getBytes(TokenAttribute.CHARSET);
        }catch (UnsupportedEncodingException except){
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
    public boolean verify(String token) {
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


    public String refresh(Cookie[] cookies) {
        //refresh token 존재 확인
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
        if(verify(tokenBuilder.toString()) == false){
            return null;
        }
        //if(refresh token이 만료된거면) refresh token 해쉬값과 db에 tokenInfoTb 와 비교해서 재발급
        Date expireDate = createExpireDate(TokenAttribute.REFRESH_EXPIRETIME);
        Map claims = getClaims(tokenBuilder.toString());
        Map headers = setHeader();
        Map paramClaims = setCliam((String)claims.get(TokenAttribute.ID_CLAIM));
        if(claims == null){
            TokenInfoDAO tokenInfoDAO = null;
            Optional<TokenInfoDAO> optionalRepo = mTokenInfoRepo.findById(tokenBuilder.toString());
            try {
                tokenInfoDAO = optionalRepo.get();
            } catch (NoSuchElementException e){
                BlacklistTokenInfoDAO blacklistTokenInfoDAO = new BlacklistTokenInfoDAO(tokenBuilder.toString());
                mBlacklistTokenInfoRepo.save(blacklistTokenInfoDAO);
            }
            tokenBuilder.delete(0, tokenBuilder.length());
            String token = create(expireDate, headers, paramClaims);
            if(token == null){
                return null;
            }
            tokenBuilder.append(token);
        } else{
            tokenBuilder.delete(0, tokenBuilder.length());
            String token = create(expireDate, headers, paramClaims);
            if(token == null){
                return null;
            }
            tokenBuilder.append(token);
        }
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

