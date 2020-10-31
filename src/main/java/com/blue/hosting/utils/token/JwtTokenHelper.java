package com.blue.hosting.utils.token;

import com.blue.hosting.entity.TokenInfoRepo;
import com.blue.hosting.services.account.eTokenField;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("jwtTokenHelper")
public class JwtTokenHelper {
    @Resource(name="tokeInfoRepo")
    private TokenInfoRepo mTokenInfoRepo;



    public String generate(String accountId, eTokenField jwtType) {

        Date date = null;
        if(jwtType == eTokenField.ACCESS_TOKEN){
            date = CreateExpireDate(eTokenField.ACCESS_TOKEN);
        } else if(jwtType == eTokenField.REFRESH_TOKEN){
            date = CreateExpireDate(eTokenField.REFRESH_TOKEN);
        }

        JwtBuilder builder = Jwts.builder()
                .setHeader(CreateHeader())
                .setClaims(CreateClaims(accountId))
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS256, createSigningKey());

        String token = builder.compact();
        return token;
    }

    private Key createSigningKey(){
        eTokenField tokenField = eTokenField.SECRETKEY;
        byte[] secretKey = tokenField.getmClaimeName().getBytes();
        String jcaName = SignatureAlgorithm.HS256.getJcaName();
        return new SecretKeySpec(secretKey, jcaName);
    }

    private Date CreateExpireDate(eTokenField jwtType) {
        Date date = new Date();
        eTokenField expireField = null;
        if(jwtType == eTokenField.ACCESS_TOKEN){
            expireField = eTokenField.ACCESS_TOKEN_EXPIREMINUTES;
        } else if(jwtType == eTokenField.REFRESH_TOKEN){
            expireField = eTokenField.REFRESH_TOKEN_EXPIREMINUTES;
        }

        long time = date.getTime() + expireField.getmExpireMinutes();
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

    private Map<String, Object> CreateClaims(String accountId){
        Map<String, Object> claims = new HashMap<>();
        eTokenField tokenField = eTokenField.ID;
        claims.put(tokenField.getmClaimeName(), accountId);
        Date date = new Date();
        tokenField = eTokenField.IAT;
        claims.put(tokenField.getmClaimeName(), date.getTime());
        return claims;
    }
}
