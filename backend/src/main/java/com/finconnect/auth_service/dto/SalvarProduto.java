package com.finconnect.auth_service.dto;

import java.util.UUID;
import com.finconnect.auth_service.entity.TipoProduto;

public record SalvarProduto(
    String nome,
    UUID categoria,
    TipoProduto tipo,
    UUID unidade,
    UUID estoque
) {}
