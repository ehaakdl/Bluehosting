package com.blue.hosting.services.account;

public enum eSecurityErrorMessage {
    INPUT_NOT_FOUND("INPUT_NOT_FOUND")
    ,RESULT_EMTY("Result Emty")
    ,OBJECT_READ_FAIL("OBJECT_READ FAIL");

    String mErrorMsg;

    eSecurityErrorMessage(String msg){
        this.mErrorMsg = msg;
    }
    String getErrorMsg(){
        return this.mErrorMsg;
    }
}
