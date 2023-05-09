package com.example.tcrud.dto;

import java.util.List;

public class JwtResponse {

    private String token;
    private String type = "Bearer";

    private Long id;
    private String username;
    private String nickname;
    private String email;
    private List<String> roles;

    public JwtResponse(String token, Long id, String username, String nickname, String email, List<String> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.roles = roles;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() { return nickname; }

    public void setNickname(String nickname) { this.nickname = nickname; }

    public List<String> getRoles() {
        return roles;
    }
}









