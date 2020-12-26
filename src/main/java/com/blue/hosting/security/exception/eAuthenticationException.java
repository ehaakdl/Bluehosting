package com.blue.hosting.security.exception;

public enum eAuthenticationException {
    AUTHENTICATION_EMPTY("Authentication empty", 1),
    OBJECT_READ_FAIL("Object Read Fail",2),
    PROVIDER_NOT_FOUND("Provider not found",3),
    TOKEN_EXPIRE("Token expire",4),
    TOKEN_NOT_VERIFY("Token not verify",5),
    ALREADY_CERTIFIED("Already Certified", 6),
    USERNOTFOUND("User Not Found", 7),
    BADCREDENTIAL("Password wrong", 8),
    EXISTUSERID("Exist User id",9);
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
