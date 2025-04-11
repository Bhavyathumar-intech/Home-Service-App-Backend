package com.example.HomeService.model;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Custom implementation of Spring Security's UserDetails interface.
 * This class represents the authenticated user and provides necessary user details.
 */
public class UserPrinciple implements UserDetails {

    @Autowired
    private Users user;

    /**
     * Constructor to initialize UserPrinciple with a Users object.
     *
     * @param user The authenticated user entity.
     */
    public UserPrinciple(Users user) {
        this.user = user;
    }

    /**
     * Retrieves the authorities granted to the user.
     *
     * @return A collection containing the user's role as a granted authority.
     */

    //For Using PreAuthorize added ROLE_
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_"+ user.getRole().name()));
    }

    /**
     * Returns the password of the user.
     *
     * @return User's password.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the username of the user.
     *
     * @return User's email.
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return true (account never expires).
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * @return true (account is not locked).
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) have expired.
     *
     * @return true (credentials never expire).
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return true (account is enabled).
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
