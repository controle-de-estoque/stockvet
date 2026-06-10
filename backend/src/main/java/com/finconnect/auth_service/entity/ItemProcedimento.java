package com.finconnect.auth_service.entity;

import java.util.UUID;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item_procedimento")
public class ItemProcedimento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "O procedimento é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedimento_id", nullable = false)
    @JsonIgnore
    private Procedimento procedimento;

    @NotNull(message = "O ID do produto é obrigatório")
    @Column(name = "produto_id", nullable = false)
    private UUID produtoId;

    @NotBlank(message = "O nome do produto é obrigatório")
    private String nome;

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    @Column(nullable = false)
    private Double quantidade;

    @NotNull(message = "O tipo de consumo é obrigatório")
    @Column(nullable = false)
    private String tipo;
}