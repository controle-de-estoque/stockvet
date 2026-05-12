package com.finconnect.auth_service.dto;

import java.util.UUID;

public record UnidadeResponse(
    UUID uuid,
    String nome, 
    int consumo
) {}
