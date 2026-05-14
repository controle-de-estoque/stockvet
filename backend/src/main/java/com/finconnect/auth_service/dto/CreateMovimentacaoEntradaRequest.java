package com.finconnect.auth_service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import com.finconnect.auth_service.entity.TipoMovimentacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateMovimentacaoEntradaRequest(
    
    @NotNull
    UUID produto,

    @NotNull
    UUID estoque,

    UUID cessionario,
    
    @NotNull
    UUID movimentadoPor,

    @NotNull
    @Positive
    int quantidade,

    @NotNull
    LocalDateTime dataHoraMovimentacao,

    @NotNull
    TipoMovimentacao tipo,

    @NotNull
    String loteId,

    LocalDate dataValidade

) {}