package com.techmart.dto;


import java.time.LocalDateTime;

public class UserResponse {
    private String email;
    private Long id;
    private LocalDateTime createAt;

    public UserResponse(Long id, String email, LocalDateTime createdAt){
        this.id = id;
        this.email = email;
        this.createAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
