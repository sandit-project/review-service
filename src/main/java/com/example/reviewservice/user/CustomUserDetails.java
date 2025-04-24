package com.example.reviewservice.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final Integer uid;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Integer uid, String username, Collection<? extends GrantedAuthority> authorities) {
        this.uid = uid;
        this.username = username;
        this.authorities = authorities;
    }

    public Integer getUid() {
        return uid;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
