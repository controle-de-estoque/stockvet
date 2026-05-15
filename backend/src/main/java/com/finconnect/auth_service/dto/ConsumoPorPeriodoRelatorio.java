package com.finconnect.auth_service.dto;

import java.time.LocalDateTime;
import com.finconnect.auth_service.entity.TipoMovimentacao;

public record ConsumoPorPeriodoRelatorio(
    TipoMovimentacao tipo,
    String produto,
    int quantidade,
    String lote,
    String movimentadoPor,
    LocalDateTime dataHora 
) {}