package com.finconnect.auth_service.entity;

import java.time.LocalDate;
import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lote {
    
    @Id
    private String identificador; //deve ser preenchido pelo usuário

    @NotNull
    private LocalDate dataValidade;

    @NotNull
    private int quantidadeRecebida;

    @NotNull
    private int quantidadeAtual;

    @NotNull
    private UUID estoque;
}
