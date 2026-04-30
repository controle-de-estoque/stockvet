package com.finconnect.auth_service.dto;

public record SignUpRequest(
    String firstName,
    String lastName,
    String email,
    String password,
    String firstPetName
) {}
