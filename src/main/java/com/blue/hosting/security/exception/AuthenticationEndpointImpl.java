package com.blue.hosting.security.exception;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
왜 인증 실패시 이쪽으로 넘어오지 않는지 확인
 */
public class AuthenticationEndpointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException except) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
    }
}
