package com.blue.hosting.entity;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;


@Entity
@Table(name="token_info_tb")
public class TokenInfoDTO {
    @Column(name = "jwt_hash", nullable=false, length=256, columnDefinition = "nvarchar2")
    @NotBlank
    @Length(max = 256)
    private String mJwtHash;

    @Id
    @Column(name = "member_id", nullable=false, length=15, columnDefinition = "nvarchar2")
    @NotBlank
    @Length(max = 15)
    private String mUsername;

    public TokenInfoDTO(){

    }

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
