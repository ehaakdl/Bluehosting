package com.blue.hosting.entity.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

public class AccountInfoVO implements UserDetails {
    public String getId() {
        return id;
    }
    public String getPasswd() {
        return passwd;
    }

    @Length(max = 15)
    @NotBlank
    private final String id;
    @Length(max = 256)
    @NotBlank
    private final String passwd;

    public String getEmail() {
        return email;
    }

    @Length(max = 256)
    @NotBlank
    private final String email;

    public AccountInfoVO(@JsonProperty("id") String id, @JsonProperty("passwd") String passwd
            ,@JsonProperty("email") String email) {
        this.id = id;
        this.passwd = passwd;
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.passwd;
    }

    @Override
    public String getUsername() {
        return this.id;
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
