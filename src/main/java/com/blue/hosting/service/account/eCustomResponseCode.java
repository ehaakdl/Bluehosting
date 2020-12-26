package com.blue.hosting.service.account;

public enum eCustomResponseCode {
    OVERLAP_ID(502),
    ALREADY_LOGIN(503),
    SUCCESS_LOGIN(200),
    FAIL_LOGIN(500),
    WRONG_FORMAT(501),
    USERNOTFOUND(504),
    WRONGPASSWD(505),
    FAIL_SIGNUP(506);

    public int getResCode() {
        return resCode;
    }

    int resCode;

    eCustomResponseCode(int resCode) {
        this.resCode = resCode;
    }
}
