package com.finconnect.auth_service.dto;

import java.time.LocalDate;
import java.util.UUID;

public record LoteResponse(
    String id,
    LocalDate dataValidade,
    int quantidadeRecebida,
    int quantidadeAtual,
    UUID produto,
    UUID estoque
) {}