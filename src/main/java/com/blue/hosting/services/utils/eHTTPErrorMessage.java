package com.blue.hosting.services.utils;

public enum eHTTPErrorMessage {
    FAIL("Fail");

    String mErrorMsg;

    eHTTPErrorMessage(String errorMsg){
        this.mErrorMsg = errorMsg;
    }
    public String getErrorMsg(){
        return this.mErrorMsg;
    }
}
