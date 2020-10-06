package com.blue.hosting.services.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;

public class AccountInfoVO implements UserDetails {
    public String getmEmail() {
        return mEmail;
    }

    public String getmId() {
        return mId;
    }

    public String getmPasswd() {
        return mPasswd;
    }

    @Length(max = 320)
    @NotNull
    @NotBlank
    private final String mEmail;
    @Length(max = 15)
    @NotNull
    @NotBlank
    private final String mId;
    @Length(max = 256)
    @NotNull
    @NotBlank
    private final String mPasswd;
    @JsonCreator
    public AccountInfoVO(@JsonProperty("email") String email,@JsonProperty("id") String id,@JsonProperty("passwd") String passwd) {
        this.mEmail = email;
        this.mId = id;
        this.mPasswd = passwd;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.mPasswd;
    }

    @Override
    public String getUsername() {
        return this.mId;
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
        return true;
    }
}
