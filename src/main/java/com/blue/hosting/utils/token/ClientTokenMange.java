package com.blue.hosting.utils.token;

import com.blue.hosting.entity.TokenInfoDAO;
import com.blue.hosting.entity.TokenInfoRepo;
import com.blue.hosting.utils.eCookie;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

@Service("clientTokenMange")
public class ClientTokenMange {
    private TokenInfoRepo mTokenInfoRepo;

    public void refreshMange(HttpServletRequest req, HttpServletResponse res) throws RuntimeException {
        Cookie[] cookies = req.getCookies();
        if(!bHaveToken(cookies)){
            throw new RuntimeException();
        }

        eTokenVal tokenType = null;
        String accountId;
        try{
            tokenType = eTokenVal.ACCESS_TOKEN;
            Cookie cook = cookSearch(tokenType.getmTokenType() ,cookies);
            if(cook == null){
               throw new Exception();
            }
            JwtTokenHelper.verifyToken(tokenType, cook.getValue());
            tokenType = eTokenVal.REFRESH_TOKEN;
            cook = cookSearch(tokenType.getmTokenType() ,cookies);
            if(cook == null){
                throw new Exception();
            }
            JwtTokenHelper.verifyToken(tokenType, cook.getValue());

        } catch (Exception except) {
            Throwable cause = except.getCause();
            if(cause == null){
                throw new RuntimeException();
                //log
            }
            if (cause instanceof ExpiredJwtException) {
                try {
                    refresh(cookies, res, tokenType);
                }catch (Exception exceptRefresh){
                    throw new RuntimeException();
                }
            }
        }
    }

    @Resource(name = "tokenInfoRepo")
    private void setmTokenInfoRepo(TokenInfoRepo mTokenInfoRepo) {
        this.mTokenInfoRepo = mTokenInfoRepo;
    }

    public void deleteToken(eTokenVal tokenType, String accountId){
        TokenInfoDAO tokenInfoDAO = new TokenInfoDAO(accountId);
        mTokenInfoRepo.delete();
    }


    public static boolean bHaveToken(Cookie[] cookies) {
        if (cookies == null) {
            return false;
        }

        int[] flags = new int[2];
        eTokenVal[] tokenType = {eTokenVal.ACCESS_TOKEN, eTokenVal.REFRESH_TOKEN};
        for (Cookie cookie : cookies) {
            for (int index = 0; index < tokenType.length; index++) {
                cookie = cookSearch(tokenType[index].getmTokenType(), cookies);
                if(cookie != null){
                    flags[index] = 1;
                }
            }
        }

        for (int flag : flags) {
            if(flag == 0){
                return false;
            }
        }

        return true;
    }

    public static Cookie cookSearch(String name, Cookie[] cookies){
        if(cookies == null){
            return null;
        }
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals(name) == true){
                return cookie;
            }
        }
        return null;
    }

    private void refresh(Cookie[] cookies, HttpServletResponse res, eTokenVal dstTokenType) throws Exception {
        JwtTokenHelper tokenHelper = new JwtTokenHelper();
        Cookie cook;
        eTokenVal tokenCheck = null;
        String token;
        String claimId;
        if(dstTokenType == eTokenVal.ACCESS_TOKEN){
            tokenCheck = eTokenVal.REFRESH_TOKEN;
        }
        if(dstTokenType == eTokenVal.REFRESH_TOKEN){
            tokenCheck = eTokenVal.ACCESS_TOKEN;
        }
        cook = cookSearch(tokenCheck.getmTokenType() ,cookies);
        Map<String, Object> claimMap = JwtTokenHelper.verifyToken(dstTokenType, cook.getValue());
        claimId =(String) claimMap.get(dstTokenType.getmIdClaimNm());
        Optional<TokenInfoDAO> tokenInfoOptional = mTokenInfoRepo.findById(claimId);
        TokenInfoDAO tokenInfoDAO = tokenInfoOptional.get();
        if(tokenCheck == eTokenVal.REFRESH_TOKEN){
            String hashFromDB = tokenInfoDAO.getmJwtHash();
            String hashFromCook = cook.getValue();
            if(hashFromDB.equals(hashFromCook) == false){
                throw new Exception();
            }
        }

        token = tokenHelper.generate(tokenInfoDAO.getmUsername(), dstTokenType);


        eCookie defCookVal = null;
        if (dstTokenType == eTokenVal.ACCESS_TOKEN) {
            defCookVal = eCookie.ACCESS_TOKEN;
        }
        if (dstTokenType == eTokenVal.REFRESH_TOKEN) {
            //디비저장
            defCookVal = eCookie.REFRESH_TOKEN;
        }
        Cookie resCook = new Cookie(defCookVal.getName(), token);
        resCook.setPath(defCookVal.getPath());
        resCook.setMaxAge(defCookVal.getMaxAge());
        res.addCookie(resCook);
    }
}
