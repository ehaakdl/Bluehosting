package com.blue.hosting.utils;

public enum eHttpHeader {
    JSON_TYPE("application/json");

    String mHeader;

    eHttpHeader(String header){
        mHeader = header;
    }

    public String getHeader(){
        return mHeader;
    }
}
