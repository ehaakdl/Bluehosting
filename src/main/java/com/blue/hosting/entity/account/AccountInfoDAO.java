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
public class AccountInfoDAO implements UserDetails{

    public AccountInfoDAO() {

    }
    public AccountInfoDAO(String mRoleAuth, String mPassword, String mUsername) {
        this.mRoleAuth = mRoleAuth;
        this.mPassword = mPassword;
        this.mUsername = mUsername;
    }

    public String getmRoleAuth() {
        return mRoleAuth;
    }

    @Column(name = "member_role", nullable=false, length=30, columnDefinition = "varchar2")
    private String mRoleAuth;

    @Column(name = "member_passwd", nullable=false, length=256, columnDefinition = "varchar2")
    private String mPassword;

    @Id
    @Column(name = "member_id", nullable=false, length=15, columnDefinition = "nvarchar2")
    private String mUsername;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return mPassword;
    }

    @Override
    public String getUsername() {
        return mUsername;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
