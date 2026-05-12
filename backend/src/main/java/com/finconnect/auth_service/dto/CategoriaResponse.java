package com.finconnect.auth_service.dto;

import java.util.UUID;

public record CategoriaResponse(
    UUID uuid,
    String nome
) {}
