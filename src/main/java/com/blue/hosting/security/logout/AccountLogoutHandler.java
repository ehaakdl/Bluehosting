package com.blue.hosting.security.logout;

import com.blue.hosting.entity.token.BlacklistTokenInfoDAO;
import com.blue.hosting.entity.token.BlacklistTokenInfoRepo;
import com.blue.hosting.entity.token.TokenInfoDAO;
import com.blue.hosting.entity.token.TokenInfoRepo;
import com.blue.hosting.utils.cookie.CookieMangement;
import com.blue.hosting.utils.token.ClientTokenMange;
import com.blue.hosting.utils.token.JwtTokenHelper;
import com.blue.hosting.utils.token.eTokenVal;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class AccountLogoutHandler implements LogoutHandler {
    @Resource(name="blacklistTokenInfoRepo")
    public void setmBlacklistTokenInfoTb(BlacklistTokenInfoRepo mBlacklistTokenInfoRepo) {
        this.mBlacklistTokenInfoRepo = mBlacklistTokenInfoRepo;
    }

    private BlacklistTokenInfoRepo mBlacklistTokenInfoRepo;

    @Resource(name="tokenInfoRepo")
    public void setmTokenInfoRepo(TokenInfoRepo mTokenInfoRepo) {
        this.mTokenInfoRepo = mTokenInfoRepo;
    }

    private TokenInfoRepo mTokenInfoRepo;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Cookie[] cookies = request.getCookies();
        eTokenVal[] tokenVal = {eTokenVal.ACCESS_TOKEN, eTokenVal.REFRESH_TOKEN };
        Cookie cook;
        String accountId;
        boolean bResult = false;
        for (eTokenVal eTokenVal : tokenVal) {
            bResult = ClientTokenMange.isSearch(eTokenVal.getmTokenType(), cookies);
            if(bResult) {
                cook = CookieMangement.search(eTokenVal.getmTokenType(), cookies);
                CookieMangement.delete(response, eTokenVal, cookies);
                Map<String, Object> claimMap = verifyToken(eTokenVal, cook.getValue());
                if(claimMap != null) {
                    BlacklistTokenInfoDAO blacklistTokenInfoDAO = new BlacklistTokenInfoDAO(cook.getValue());
                    mBlacklistTokenInfoRepo.saveAndFlush(blacklistTokenInfoDAO);
                    accountId = (String) claimMap.get(eTokenVal.getmIdClaimNm());
                    TokenInfoDAO tokenInfoDAO = findTokenInfoTb(accountId);
                    if(tokenInfoDAO == null) {
                        continue;
                    }else{
                        blacklistTokenInfoDAO = new BlacklistTokenInfoDAO(tokenInfoDAO.getmJwtHash());
                        mBlacklistTokenInfoRepo.saveAndFlush(blacklistTokenInfoDAO);
                    }
                    mTokenInfoRepo.delete(tokenInfoDAO);
                }
            }
        }
        if(bResult == false){
            return;
        }

        //토크 만료날짜에 블랙리스트에서 지워야함
    }

    private Map<String, Object> verifyToken(eTokenVal tokenVal, String token){
        Map<String, Object> claimMap = new HashMap<>();
        try {
            claimMap = JwtTokenHelper.verifyToken(tokenVal, token);
        } catch (Exception except) {
            Throwable throwCause = except.getCause();
            if(throwCause == null){
                return null;
            }
            else if((throwCause instanceof ExpiredJwtException) == false){
                return null;
            }
        }
        return claimMap;
    }


    private TokenInfoDAO findTokenInfoTb(String accountId){
        Optional<TokenInfoDAO> tokenInfoOptional = mTokenInfoRepo.findById(accountId);
        TokenInfoDAO tokenInfoDAO;
        try {
            tokenInfoDAO = tokenInfoOptional.get();
        }catch (NoSuchElementException except){
            return null;
        }
        return tokenInfoDAO;
    }

}
