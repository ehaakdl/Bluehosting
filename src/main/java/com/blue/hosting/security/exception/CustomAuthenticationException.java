package com.blue.hosting.security.exception;

import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException {
    public int getCode() {
        return code;
    }

    private int code;
    public CustomAuthenticationException(eAuthenticationException e) {
        super(e.getMsg());
        code = e.getCode();
    }
}
