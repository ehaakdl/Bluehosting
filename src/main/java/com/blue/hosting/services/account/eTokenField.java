package com.blue.hosting.services.account;

public enum eTokenField {
    EMAIL("email"),
    ID("id"),
    PASSWD("passwd"),
    TYPE("JWT"),
    ALG("HS256"),
    ExpireMinutes(15),
    ACCESS_TOKEN("access_token");

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
