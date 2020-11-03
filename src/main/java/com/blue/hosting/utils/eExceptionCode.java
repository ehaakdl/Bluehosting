package com.blue.hosting.utils;

public enum eExceptionCode {
    RESULT_EMTY("Result Emty")
    ,OBJECT_READ_FAIL("OBJECT_READ FAIL")
    ,USERNAME_NOT_FOUND("USERNAME_NOT_FOUND");

    String mExceptMsg;
    eExceptionCode(String msg){
        this.mExceptMsg = msg;
    }
    public String getmExceptMsg(){
        return this.mExceptMsg;
    }
}
