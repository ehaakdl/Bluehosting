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
    public AccountInfoDAO(String roleAuth, String password, String username, String email) {
        this.roleAuth = roleAuth;
        this.password = password;
        this.username = username;
        this.email = email;
    }

    public String getRoleAuth() {
        return roleAuth;
    }

    public String getEmail() {
        return email;
    }

    @Column(name = "member_email", nullable=false, length=256, columnDefinition = "nvarchar2")
    private String email;

    @Column(name = "member_role", nullable=false, length=30, columnDefinition = "varchar2")
    private String roleAuth;

    @Column(name = "member_passwd", nullable=false, length=256, columnDefinition = "varchar2")
    private String password;

    public AccountInfoDAO(String username) {
        this.username = username;
    }

    @Id
    @Column(name = "member_id", nullable=false, length=15, columnDefinition = "nvarchar2")
    private String username;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
