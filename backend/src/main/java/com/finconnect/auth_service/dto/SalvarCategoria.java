package com.finconnect.auth_service.dto;

import java.util.UUID;

public record SalvarCategoria(
    String nome,
    UUID estoque
) {}
