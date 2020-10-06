package com.blue.hosting.services.utils;

import com.blue.hosting.entity.TokenInfoRepo;
import com.blue.hosting.entity.TokenInfoTb;
import com.blue.hosting.services.account.AccountInfoVO;
import com.blue.hosting.services.account.eTokenField;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JwtTokenHelper {
    private TokenInfoRepo mTokenInfoRepo;

    public String generate(AccountInfoVO accountInfoVO) {
        StringBuilder secretKeyBuilder = new StringBuilder();
        JwtBuilder builder = Jwts.builder()
                .setSubject(accountInfoVO.getmEmail())
                .setHeader(CreateHeader())
                .setClaims(CreateClaims(accountInfoVO))
                .setExpiration(CreateDate())
                .signWith(SignatureAlgorithm.HS256, createSigningKey(secretKeyBuilder));

        String secretKey = new String(secretKeyBuilder);
        String token = builder.compact();

        TokenInfoTb tokenInfoTb = new TokenInfoTb();
        tokenInfoTb.setmSecretKey(secretKey);
        String hash = token.substring(token.lastIndexOf(".") + 1);
        tokenInfoTb.setmJwtHash(hash);
        tokenInfoTb.setmUsername(accountInfoVO.getUsername());
        mTokenInfoRepo.save(tokenInfoTb);

        return token;
    }

    private Key createSigningKey(StringBuilder secretKeyBuilder){
        Date date = new Date();
        secretKeyBuilder.append(date.toString());
        secretKeyBuilder.append('-');
        secretKeyBuilder.append(mTokenInfoRepo.getTokenInfoSecrtKeyParseSeqNextValue());

        String secretKey = new String(secretKeyBuilder);
        byte[] secretKeyBytes = secretKey.getBytes();
        String jcaName = SignatureAlgorithm.HS256.getJcaName();
        return new SecretKeySpec(secretKeyBytes, jcaName);
    }

    private Date CreateDate() {
        Date date = new Date();
        eTokenField tokenField = eTokenField.ExpireMinutes;
        long time = date.getTime() * 1000 * 60 * tokenField.getmExpireMinutes();
        date.setTime(time);
        return date;
    }

    private Map<String, Object> CreateHeader() {
        Map<String, Object> header = new HashMap<>();
        eTokenField tokenField = eTokenField.TYPE;
        header.put("typ", tokenField.getmClaimeName());
        tokenField = eTokenField.ALG;
        header.put("alg", tokenField.getmClaimeName());
        header.put("regDate", System.currentTimeMillis());
        return header;
    }

    private Map<String, Object> CreateClaims(AccountInfoVO accountInfoVO){
        Map<String, Object> claims = new HashMap<>();
        eTokenField tokenField = eTokenField.EMAIL;
        claims.put(tokenField.getmClaimeName(), accountInfoVO.getmEmail());
        tokenField = eTokenField.ID;
        claims.put(tokenField.getmClaimeName(), accountInfoVO.getmId());
        tokenField = eTokenField.PASSWD;
        claims.put(tokenField.getmClaimeName(), accountInfoVO.getmPasswd());
        return claims;
    }
}
