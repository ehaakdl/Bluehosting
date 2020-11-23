package com.blue.hosting.security.exception;

public enum eAuthenticationException {
    AUTHENTICATION_EMPTY("Authentication empty", 1),
    OBJECT_READ_FAIL("Object Read Fail",2),
    PROVIDER_NOT_FOUND("Provider not found",3),
    TOKEN_EXPIRE("Token expire",4),
    TOKEN_NOT_VERIFY("Token not verify",5);
    eAuthenticationException(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    private int code;
    private String msg;

}
