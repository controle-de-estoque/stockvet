package com.finconnect.auth_service.dto;

public record ResetPasswordRequest(
    String email,
    String password,
    String firstPetName
) {}
