package com.tlu.thuvien.api.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String Token;
    private String tokenType = "Bearer";
    private String role;
    public AuthResponse(String accessToken) {
        this.Token = accessToken;
    }
}