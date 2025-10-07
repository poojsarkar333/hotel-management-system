package com.hotel.staff_service.entity;

public class LoginResponse {

    private String token;

    public LoginResponse() {}
    public LoginResponse(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
