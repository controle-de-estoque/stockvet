package com.finconnect.auth_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateMovimentacaoProcedimentoRequest(
    @NotNull UUID procedimentoId,
    @NotNull @Positive Double pesoAnimal,
    @NotNull UUID estoque,
    @NotNull UUID movimentadoPor,
    @NotNull LocalDateTime dataHoraMovimentacao,
    UUID cessionario
) {}