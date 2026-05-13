package com.finconnect.auth_service.dto;

import java.time.LocalDate;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;

public record CreateLoteRequest(
    
    @NotNull
    String id,

    @NotNull
    LocalDate dataValidade,
    
    @NotNull
    int quantidadeRecebida,
    
    @NotNull
    UUID estoque

) {}