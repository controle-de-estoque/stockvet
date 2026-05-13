package com.finconnect.auth_service.dto;

import java.time.LocalDate;

public record LoteResponse(
    String id,
    LocalDate dataValidade,
    int quantidadeRecebida,
    int quantidadeAtual
) {}