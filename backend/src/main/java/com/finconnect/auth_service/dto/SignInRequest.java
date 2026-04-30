package com.finconnect.auth_service.dto;

public record SignInRequest(
    String username,
    String password
) {}
