package com.blue.hosting.services.account;

public enum eTokenField {
    ID("id"),
    IAT("iat"),
    TYPE("JWT"),
    ALG("HS256"),
    ACCESS_TOKEN_EXPIREMINUTES(15 * 1000 * 60),
    REFRESH_TOKEN_EXPIREMINUTES( 24 * 1000 * 3600),
    ACCESS_TOKEN("access_token"),
    REFRESH_TOKEN("refresh_token"),
    SECRETKEY("hellow world");

    private String mClaimeName;
    private int mExpireMinutes;

    eTokenField(String claimName){
        this.mClaimeName = claimName;
    }
    eTokenField(int minute){
        this.mExpireMinutes = minute;
    }
    public String getmClaimeName() {
        return mClaimeName;
    }

    public int getmExpireMinutes() {
        return mExpireMinutes;
    }
}
