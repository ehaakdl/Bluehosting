package com.blue.hosting.entity.email;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="email_state_tb")
public class EmailStateDAO {
    public EmailStateDAO(String email, char authenticatedFlag, long expireTime, int code) {
        this.email = email;
        this.authenticatedFlag = authenticatedFlag;
        this.expireTime = expireTime;
        this.code = code;
    }

    public EmailStateDAO(){

    }

    @Id
    @Column(name = "email", nullable=false, length=256, columnDefinition = "nvarchar2")
    private String email;


    @Column(name = "authenticated_flag", nullable=false, length=1, columnDefinition = "char")
    private char authenticatedFlag;

    @Column(name = "expire_time", nullable=false, columnDefinition = "int")
    private long expireTime;

    public String getEmail() {
        return email;
    }

    public char getAuthenticatedFlag() {
        return authenticatedFlag;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public int getCode() {
        return code;
    }

    @Column(name = "code", nullable=false, columnDefinition = "int")
    private int code;
}
