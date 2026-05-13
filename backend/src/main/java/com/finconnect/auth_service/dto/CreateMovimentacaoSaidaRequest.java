package com.finconnect.auth_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateMovimentacaoSaidaRequest(
    
    @NotNull(message = "O ID do produto é obrigatório")
    UUID produto,

    @NotNull(message = "O ID do estoque é obrigatório")
    UUID estoque,

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade de saída deve ser maior que zero")
    int quantidade,

    @NotNull(message = "O ID do usuário movimentador é obrigatório")
    UUID movimentadoPor,

    @NotNull(message = "A data e hora da movimentação são obrigatórias")
    LocalDateTime dataHoraMovimentacao
) {}