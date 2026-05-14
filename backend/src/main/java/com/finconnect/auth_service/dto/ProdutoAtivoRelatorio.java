package com.finconnect.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProdutoAtivoRelatorio(
    
    @NotBlank
    String nome,
    
    @NotBlank
    String categoria,

    @NotNull
    @Positive
    int quantidade,

    @NotBlank
    @Positive
    int quantidadeCritica,

    @NotBlank
    String unidade
) {}