package com.finconnect.auth_service.entity;

import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"estoque", "nome"}))
@Data
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String nome;
    
    @NotNull
    private UUID categoria;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoProduto tipo;

    @NotNull
    private UUID unidade;

    @NotNull
    private UUID estoque;

    @NotNull
    @Positive
    private int quantidadeCritica;

    @NotNull
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean ativo = true;
}
