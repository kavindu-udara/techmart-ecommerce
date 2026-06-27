package com.techmart.config;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AuthenticatedUser {
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
