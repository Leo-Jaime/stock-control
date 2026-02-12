package com.stockcontrol.dto;

public class AuthResponse {
    public String token;
    public String username;
    public long expiresIn;

    public AuthResponse(String token, String username, long expiresIn) {
        this.token = token;
        this.username = username;
        this.expiresIn = expiresIn;
    }
}
