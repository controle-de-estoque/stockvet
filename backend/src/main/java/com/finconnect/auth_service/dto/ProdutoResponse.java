package com.finconnect.auth_service.dto;

public record ProdutoResponse(
    String nome,
    String categoria,
    String tipo,
    String unidade,
    int quantidadeCritica,
    int quantidade
) {}
