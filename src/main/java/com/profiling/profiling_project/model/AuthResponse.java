package com.profiling.profiling_project.model;

import lombok.Data;
import lombok.Getter;

@Data
public class AuthResponse {
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    // Getter


}