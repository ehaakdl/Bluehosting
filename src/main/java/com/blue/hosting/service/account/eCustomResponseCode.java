package com.blue.hosting.service.account;

public enum eCustomResponseCode {
    OVERLAP_ID(201),
    ALREADY_LOGIN(202);

    public int getResCode() {
        return resCode;
    }

    int resCode;

    eCustomResponseCode(int resCode) {
        this.resCode = resCode;
    }
}
