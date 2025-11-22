package com.bezkoder.springjwt.payload.request;

import jakarta.validation.constraints.NotEmpty;

public class TokenValidationRequest {

    @NotEmpty
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
