package com.finconnect.auth_service.dto;

import java.util.UUID;

import com.finconnect.auth_service.entity.Tipo;

public record ProdutoResponse(
    String nome,
    UUID categoria,
    Tipo tipo,
    UUID unidade
) {}
