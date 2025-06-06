package com.gestion.zarpas_backend.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}