package com.blue.hosting.services.account;

public enum eSecurityVal {
    INPUT_NOT_FOUND("INPUT_NOT_FOUND")
    ,RESULT_EMTY("Result Emty")
    ,OBJECT_READ_FAIL("OBJECT_READ FAIL");


    String mErrorMsg;
    eSecurityVal(String msg){
        this.mErrorMsg = msg;
    }
    public String getmErrorMsg(){
        return this.mErrorMsg;
    }
}
