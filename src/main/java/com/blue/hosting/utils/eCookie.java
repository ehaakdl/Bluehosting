package com.blue.hosting.utils;

public enum eCookie {
    ACCESS_TOKEN("ACCESS_TOKEN", 15 * 60, "/"),
    REFRESH_TOKEN("REFRESH_TOKEN",3600 * 24, "/"),
    JSON_TYPE("application/json", 0, "/");

    String mName;
    int mMaxAge;
    String mPath;

    eCookie(String name, int maxAge, String path){
        mName = name;
        mMaxAge = maxAge;
        mPath = path;
    }

    public String getName(){
        return this.mName;
    }
    public int getMaxAge(){return this.mMaxAge;}
    public String getPath(){
        return mPath;
    }
}
