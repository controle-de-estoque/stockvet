package com.finconnect.auth_service.dto;

import java.util.UUID;

public record SalvarUnidade (
    String nome,
    int consumoMinimo,
    UUID estoque
) {}
