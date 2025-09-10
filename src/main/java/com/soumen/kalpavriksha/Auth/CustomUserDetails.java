package com.soumen.kalpavriksha.Auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails
{
    private final String userId;
    private final String password;
    @Getter
    private final String role;
    @Getter
    private final boolean isVerified;

    public CustomUserDetails(String userId, String password, String role, boolean isVerified)
    {
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.isVerified = isVerified;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.role));

        if(this.isVerified)
        {
            authorities.add(new SimpleGrantedAuthority("USER_VERIFIED"));
        }

        return authorities;
    }

    @Override
    public String getPassword()
    {
        return this.password;
    }

    @Override
    public String getUsername()
    {
        return this.userId;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return this.isVerified; // enable only verified users
    }

}
