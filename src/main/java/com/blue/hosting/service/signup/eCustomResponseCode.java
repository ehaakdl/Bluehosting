package com.blue.hosting.service.signup;

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
