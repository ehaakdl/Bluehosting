package com.blue.hosting.utils.token;

import io.jsonwebtoken.*;

import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTokenHelper {
    public String generate(String accountId, eTokenVal tokenType) throws UnsupportedEncodingException {
        Date date = CreateExpireDate(tokenType);
        JwtBuilder builder = Jwts.builder()
                .setHeader(CreateHeader(tokenType))
                .setClaims(CreateClaims(tokenType ,accountId))
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS256, createSigningKey(tokenType));

        String token = builder.compact();
        return token;
    }

    public static Map<String, Object> verifyToken(eTokenVal tokenType, String token) throws Exception {
        Map<String, Object> claimMap = null;
        try {
            String secretKey = tokenType.getmSecretKey();
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes(tokenType.getmEncodeType()))
                    .parseClaimsJws(token)
                    .getBody();

            claimMap = claims;
        } catch (ExpiredJwtException except) {
            throw (Exception)new Exception().initCause(except);
        } catch (Exception except) {
            throw new Exception();
        }
        return claimMap;
    }

    private Key createSigningKey(eTokenVal tokenType) throws UnsupportedEncodingException{
        String secretKey = tokenType.getmSecretKey();
        byte[] secretKeyBytes = secretKey.getBytes(tokenType.getmEncodeType());

        String jcaName = SignatureAlgorithm.HS256.getJcaName();
        return new SecretKeySpec(secretKeyBytes, jcaName);
    }

    private Date CreateExpireDate(eTokenVal tokenType) {
        Date date = new Date();
        long time = date.getTime() + tokenType.getmExpireVal();
        date.setTime(time);
        return date;
    }

    private Map<String, Object> CreateHeader(eTokenVal tokenType) {
        Map<String, Object> header = new HashMap<>();
        header.put(tokenType.getmTypHeaderNm(), tokenType.getmTypVal());
        header.put(tokenType.getmAlgHeaderNm(), tokenType.getmAlgVal());
        return header;
    }

    private Map<String, Object> CreateClaims(eTokenVal tokenType, String accountId){
        Map<String, Object> claims = new HashMap<>();
        claims.put(tokenType.getmIdClaimNm(), accountId);
        Date date = new Date();
        claims.put(tokenType.getmIatClaimNm(), date.getTime());
        return claims;
    }
}
