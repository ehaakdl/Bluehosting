package com.blue.hosting.entity.email;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="email_state_tb")
public class EmailStateDAO {
    public EmailStateDAO(String mEmail, String mAuthenticatedFlag, long mExpireTime, int mCode) {
        this.mEmail = mEmail;
        this.mAuthenticatedFlag = mAuthenticatedFlag;
        this.mExpireTime = mExpireTime;
        this.mCode = mCode;
    }

    public EmailStateDAO(){

    }

    @Id
    @Column(name = "email", nullable=false, length=256, columnDefinition = "nvarchar2")
    private String mEmail;

    public String getmEmail() {
        return mEmail;
    }

    public String getmAuthenticatedFlag() {
        return mAuthenticatedFlag;
    }

    public long getmExpireTime() {
        return mExpireTime;
    }

    public int getmCode() {
        return mCode;
    }

    @Column(name = "authenticated_flag", nullable=false, length=1, columnDefinition = "char")
    private String mAuthenticatedFlag;

    @Column(name = "expire_time", nullable=false, columnDefinition = "int")
    private long mExpireTime;

    @Column(name = "code", nullable=false, columnDefinition = "int")
    private int mCode;
}
