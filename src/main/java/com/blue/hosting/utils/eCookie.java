package com.blue.hosting.utils;

public enum eCookie {
    ACCESS_TOKEN("ACCESS_TOKEN", 15 * 60, "/"),
    REFRESH_TOKEN("REFRESH_TOKEN",60 * 60 * 24, "/"),
    JSON_TYPE("application/json", 0, "/");

    String mName;
    int mMaxAge;
    String mDomain;

    eCookie(String name, int maxAge, String domain){
        mName = name;
        mMaxAge = maxAge;
        mDomain = domain;
    }

    public String getName(){
        return this.mName;
    }
    public int getMaxAge(){return this.mMaxAge;}
    public String getDomain(){
        return mDomain;
    }
}
