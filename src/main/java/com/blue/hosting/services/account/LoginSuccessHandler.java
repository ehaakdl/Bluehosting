package com.blue.hosting.services.account;

import com.blue.hosting.services.utils.JwtTokenHelper;
import com.blue.hosting.services.utils.eHTTPErrorMessage;
import com.blue.hosting.services.utils.eHTTPHeader;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        AccountInfoVO accountInfoVO = (AccountInfoVO) authentication.getPrincipal();
        eHTTPHeader authHeader = null;
        eHTTPErrorMessage errorMsg = null;
        String resVal = null;
        try {
            JwtTokenHelper jwtTokenHelper = new JwtTokenHelper();
            resVal = jwtTokenHelper.generate(accountInfoVO);
            authHeader = eHTTPHeader.Auth;
        }catch(DataAccessException dataAccessExcept){
            authHeader = eHTTPHeader.ERROR;
            errorMsg = eHTTPErrorMessage.FAIL;
            resVal = errorMsg.getErrorMsg();
        }finally {
            response.addHeader(authHeader.getHeader(), resVal);
        }
    }
}
