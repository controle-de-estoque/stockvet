package com.finconnect.auth_service.entity;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    private LocalDate dataValidade;

    @NotNull
    private int quantidadeRecebida;

    @NotNull
    private int quantidadeAtual;

    @ManyToOne
    @JoinColumn(name = "estoque_id", nullable = false)
    private Estoque estoque;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;
}
