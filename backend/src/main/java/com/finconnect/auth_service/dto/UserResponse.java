package com.finconnect.auth_service.dto;

import java.util.UUID;

public record UserResponse(
    UUID id,
    String nome,
    String email
) {}
