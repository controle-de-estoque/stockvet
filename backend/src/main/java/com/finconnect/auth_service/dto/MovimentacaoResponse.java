package com.finconnect.auth_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record MovimentacaoResponse(
    UUID id,
    String tipo,
    String movimentadoPor,
    String cedidoA,
    LocalDateTime dataHoraMovimentacao
) {}
