package com.blue.hosting.services.utils;

public enum eHTTPHeader {
    Auth("Auth"),
    ERROR("Error");

    String mheader;

    eHTTPHeader(String header){
        this.mheader = header;
    }
    public String getHeader(){
        return this.mheader;
    }
}
