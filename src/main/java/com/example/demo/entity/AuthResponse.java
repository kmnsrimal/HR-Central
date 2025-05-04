package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    @JsonProperty("access_token")
    private final String accessToken;

    @JsonProperty("expires_in")
    private final long expiresIn;

    @JsonProperty("token_type")
    private final String tokenType;
}
