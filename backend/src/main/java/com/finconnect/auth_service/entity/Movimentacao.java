package com.finconnect.auth_service.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
@Data
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TipoMovimentacao tipo;

    @NotNull
    private UUID produto;

    @NotNull
    @Positive
    private int quantidade;

    @NotNull
    private LocalDateTime dataHoraMovimentacao;

    private UUID cessionario;

    @NotNull
    private UUID movimentadorPor;

    @NotNull
    private UUID estoque;

}
