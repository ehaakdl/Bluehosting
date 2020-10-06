package com.blue.hosting.entity;

import javax.persistence.*;


@Entity
@Table(name="token_info_tb")
@SequenceGenerator(
        name = "token_info_secret_key_parse_seq_generator",
        sequenceName = "token_info_secret_key_parse_seq", //매핑할 데이터베이스 시퀀스 이름
        initialValue = 1512342, allocationSize = 1)
public class TokenInfoTb {
    @Column(name = "jwt_hash", nullable=false, length=256, columnDefinition = "nvarchar2")
    private String mJwtHash;

    @Id
    @Column(name = "member_id", nullable=false, length=15, columnDefinition = "nvarchar2")
    private String mUsername;


    public void setmJwtHash(String mJwtHash) {
        this.mJwtHash = mJwtHash;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public void setmSecretKey(String mSecretKey) {
        this.mSecretKey = mSecretKey;
    }
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "token_info_secret_key_parse_seq_generator")
    @Column(name = "secret_key", nullable=false, length=20, columnDefinition = "nvarchar2")
    private String mSecretKey;

    public String getmUsername() {
        return mUsername;
    }

    public String getmSecretKey() {
        return mSecretKey;
    }

    public String getmJwtHash() {
        return mJwtHash;
    }
}
