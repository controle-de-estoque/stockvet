package com.finconnect.auth_service.dto;

import java.util.UUID;

public record ProdutoResponse(
    UUID id,
    String nome,
    String categoria,
    String tipo,
    String unidade,
    int quantidadeCritica,
    int quantidade,
    boolean ativo
) {}
