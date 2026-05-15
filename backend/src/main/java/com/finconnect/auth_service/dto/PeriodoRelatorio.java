package com.finconnect.auth_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;

public record PeriodoRelatorio(

    LocalDateTime inicio,
    
    LocalDateTime fim,

    @NotNull
    UUID estoque
) {}