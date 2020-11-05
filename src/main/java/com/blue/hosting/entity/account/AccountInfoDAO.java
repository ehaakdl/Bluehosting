package com.blue.hosting.entity.account;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Collection;


@Entity
@Table(name="member_info_tb")
public class AccountInfoDAO {


    @Column(name = "member_passwd", nullable=false, length=256, columnDefinition = "varchar2")
    private String mPassword;

    @Id
    @Column(name = "member_id", nullable=false, length=15, columnDefinition = "nvarchar2")
    private String mUsername;

    public String getmPassword() {
        return mPassword;
    }

    public String getmUsername() {
        return mUsername;
    }
}