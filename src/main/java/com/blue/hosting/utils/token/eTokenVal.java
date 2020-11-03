package com.blue.hosting.utils.token;

public enum eTokenVal {
    ACCESS_TOKEN("ACCESS_TOKEN", "hellow world", "UTF-8",
            15 * 1000 * 60, "id", "iat", "JWT", "HS256","typ","alg"),
    REFRESH_TOKEN("REFRESH_TOKEN", "hellow world", "UTF-8",
            1000 * 3600 * 2, "id", "iat", "JWT", "HS256","typ","alg");

    private String mTokenType;

    eTokenVal(String mTokenType, String mSecretKey, String mEncodeType, int mExpireVal,
              String mIdClaimNm, String mIatClaimNm, String mTypVal, String mAlgVal,
              String mTypHeaderNm, String mAlgHeaderNm) {
        this.mTokenType = mTokenType;
        this.mSecretKey = mSecretKey;
        this.mEncodeType = mEncodeType;
        this.mExpireVal = mExpireVal;
        this.mIdClaimNm = mIdClaimNm;
        this.mIatClaimNm = mIatClaimNm;
        this.mTypVal = mTypVal;
        this.mAlgVal = mAlgVal;
        this.mTypHeaderNm = mTypHeaderNm;
        this.mAlgHeaderNm = mAlgHeaderNm;
    }

    private String mSecretKey;

    public String getmTokenType() {
        return mTokenType;
    }

    public String getmSecretKey() {
        return mSecretKey;
    }

    public String getmEncodeType() {
        return mEncodeType;
    }

    public int getmExpireVal() {
        return mExpireVal;
    }

    public String getmIdClaimNm() {
        return mIdClaimNm;
    }

    public String getmIatClaimNm() {
        return mIatClaimNm;
    }

    public String getmTypVal() {
        return mTypVal;
    }

    public String getmAlgVal() {
        return mAlgVal;
    }

    public String getmTypHeaderNm() {
        return mTypHeaderNm;
    }

    public String getmAlgHeaderNm() {
        return mAlgHeaderNm;
    }
    private String mTypVal;
    private String mAlgVal;
    private String mEncodeType;
    private int mExpireVal;
    private String mIdClaimNm;
    private String mIatClaimNm;
    private String mTypHeaderNm;
    private String mAlgHeaderNm;



}
