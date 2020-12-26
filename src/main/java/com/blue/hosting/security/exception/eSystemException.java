package com.blue.hosting.security.exception;

public enum eSystemException {
    DELETE_ALL_TOKEN_FAIL("Delete fail access token, refresh token", 100),
    CREATE_FAIL_TOKEN("Create Fail Token", 101),
    INSERT_FAIL_TOKEN_INFO_TABLE("Insert Fail TokenInfo Table", 102),
    INSERT_FAIL_ACCOUNT_INFO_TABLE("Insert Fail AccountInfo Table", 103);
    eSystemException(String msg, int code) {
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
