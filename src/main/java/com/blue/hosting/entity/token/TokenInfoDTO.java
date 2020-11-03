package com.blue.hosting.entity.token;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;


public class TokenInfoDTO {
    @NotBlank
    @Length(max = 256)
    private String mJwtHash;

    @NotBlank
    @Length(max = 15)
    private String mUsername;

    public TokenInfoDTO(String hash, String userName){
        mJwtHash = hash;
        mUsername = userName;
    }

    public void setmJwtHash(String mJwtHash) {
        this.mJwtHash = mJwtHash;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getmUsername() {
        return mUsername;
    }

    public String getmJwtHash() {
        return mJwtHash;
    }
}
