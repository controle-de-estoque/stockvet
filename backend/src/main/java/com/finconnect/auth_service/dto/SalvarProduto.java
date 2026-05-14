package com.finconnect.auth_service.dto;

import java.util.UUID;
import com.finconnect.auth_service.entity.TipoProduto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SalvarProduto(
    
    @NotBlank
    String nome,

    @NotNull
    UUID categoria,
    
    @NotNull
    TipoProduto tipo,
    
    @NotNull
    UUID unidade,
    
    @NotNull
    UUID estoque,
    
    @NotNull
    @Positive
    int quantidadeCritica
) {}
