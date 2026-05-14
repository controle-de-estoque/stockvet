package com.finconnect.auth_service.dto;

import java.util.UUID;

public record CessionarioResponse(
    UUID id,
    String nome,
    String email
) {}