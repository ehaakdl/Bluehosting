package com.blue.hosting.utils.token;

import com.blue.hosting.entity.token.BlacklistTokenInfoDAO;
import com.blue.hosting.entity.token.BlacklistTokenInfoRepo;
import com.blue.hosting.entity.token.TokenInfoDAO;
import com.blue.hosting.entity.token.TokenInfoRepo;
import com.blue.hosting.utils.cookie.CookieMangement;
import com.blue.hosting.utils.cookie.eCookie;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.Option;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component("clientTokenMange")
public class ClientTokenMange {
    private TokenInfoRepo mTokenInfoRepo;

    @Resource(name="blacklistTokenInfoRepo")
    public void setmBlacklistTokenInfoRepo(BlacklistTokenInfoRepo mBlacklistTokenInfoRepo) {
        this.mBlacklistTokenInfoRepo = mBlacklistTokenInfoRepo;
    }

    private BlacklistTokenInfoRepo mBlacklistTokenInfoRepo;
    private TokenInfoDAO findTokenInfo(String hash){
        Optional<TokenInfoDAO> optional = mTokenInfoRepo.findById(hash);
        TokenInfoDAO tokenInfoDAO;
        try{
            tokenInfoDAO = optional.get();
        } catch(NoSuchElementException except){
            return null;
        }
        return tokenInfoDAO;
    }

    private boolean verify(eTokenVal tokenVal, String hash){
        try {
            if(JwtTokenHelper.verifyToken(tokenVal, hash) == null){
                return false;
            }
        }catch (ExpiredJwtException except){
            return true;
        }
        return true;
    }
    public boolean isSearchBlacklist(String hash){
        Optional<BlacklistTokenInfoDAO> optional = mBlacklistTokenInfoRepo.findById(hash.toString());
        try {
            optional.get();
        }catch (NoSuchElementException except){
            return false;
        }
        return true;
    }

    public void refresh(HttpServletRequest req, HttpServletResponse res) {
        Cookie[] cookies = req.getCookies();
        eTokenVal tokenVal = eTokenVal.REFRESH_TOKEN;
        Cookie cook = CookieMangement.search(tokenVal.getmTokenType(), cookies);
        if(cook == null){
            return;
        }

        StringBuilder hash = new StringBuilder(cook.getValue());
        TokenInfoDAO tokenInfoDAO;
        if(isSearchBlacklist(hash.toString())){
            return;
        }
        BlacklistTokenInfoDAO blacklistTokenInfoDAO;
        if(verify(tokenVal, hash.toString()) == false){
            blacklistTokenInfoDAO = new BlacklistTokenInfoDAO(hash.toString());
            mBlacklistTokenInfoRepo.saveAndFlush(blacklistTokenInfoDAO);
            return;
        }

        tokenInfoDAO = findTokenInfo(hash.toString());
        if(tokenInfoDAO == null){
            blacklistTokenInfoDAO = new BlacklistTokenInfoDAO(hash.toString());
            mBlacklistTokenInfoRepo.saveAndFlush(blacklistTokenInfoDAO);
            return;
        }
        JwtTokenHelper tokenHelper = new JwtTokenHelper();
        String id = tokenInfoDAO.getmUsername();
        try {
            tokenHelper.verifyToken(tokenVal, hash.toString());
        }catch (ExpiredJwtException except){
            String token = tokenHelper.generate(id, tokenVal);
            if(token == null){
                blacklistTokenInfoDAO = new BlacklistTokenInfoDAO(hash.toString());
                mBlacklistTokenInfoRepo.saveAndFlush(blacklistTokenInfoDAO);
                return;
            }
            mTokenInfoRepo.deleteById(hash.toString());
            hash.delete(0, hash.length());
            hash.append(token);
            tokenInfoDAO = new TokenInfoDAO(hash.toString(), tokenInfoDAO.getmUsername());

            mTokenInfoRepo.saveAndFlush(tokenInfoDAO);
            CookieMangement.add(res,eCookie.REFRESH_TOKEN, hash.toString());
        }
        String token;
        tokenVal = eTokenVal.ACCESS_TOKEN;
        Map<String, Object> claimMap = null;
        cook = CookieMangement.search(tokenVal.getmTokenType(), cookies);
        if(cook != null){
            if(isSearchBlacklist(hash.toString())){
                CookieMangement.delete(res,eTokenVal.ACCESS_TOKEN, req.getCookies());
                return;
            }
            hash.delete(0, hash.length());
            hash.append(cook.getValue());
            try{
                claimMap = tokenHelper.verifyToken(tokenVal, hash.toString());
                if(claimMap == null){
                    CookieMangement.delete(res,eTokenVal.ACCESS_TOKEN, req.getCookies());
                    blacklistTokenInfoDAO = new BlacklistTokenInfoDAO(hash.toString());
                    mBlacklistTokenInfoRepo.saveAndFlush(blacklistTokenInfoDAO);
                    return;
                }
            }catch (ExpiredJwtException except){
                token = tokenHelper.generate(id, tokenVal);
                if(token == null){
                    return;
                }
                hash.append(token);
                CookieMangement.add(res,eCookie.ACCESS_TOKEN, hash.toString());
            }
        } else {
            token = tokenHelper.generate(id, tokenVal);
            if(token == null){
                return;
            }
            hash.append(token);
            CookieMangement.add(res,eCookie.ACCESS_TOKEN, hash.toString());
        }
    }

    @Resource(name = "tokenInfoRepo")
    private void setmTokenInfoRepo(TokenInfoRepo mTokenInfoRepo) {
        this.mTokenInfoRepo = mTokenInfoRepo;
    }


    public static boolean isSearch(String tokenName,Cookie[] cookies) {
        if (cookies == null) {
            return false;
        }
        for (Cookie cookie : cookies) {
                cookie = CookieMangement.search(tokenName, cookies);
                if(cookie != null){
                    return true;
                }
        }
        return false;
    }
}
