package com.finconnect.auth_service.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.finconnect.auth_service.dto.Genero;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"estoque", "nome", "genero"}))
public class Procedimento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "O nome do procedimento é obrigatório")
    private String nome;

    @NotBlank(message = "A espécie é obrigatória")
    private String especie;

    @NotNull(message = "O gênero é obrigatório")
    @Enumerated(EnumType.STRING)
    private Genero genero;

    @NotNull(message = "O ID do estoque é obrigatório")
    private UUID estoque;

    @NotNull
    @Column(name = "is_ativo", nullable = false)
    private boolean ativo = true;
    
    @OneToMany(mappedBy = "procedimento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemProcedimento> itens = new ArrayList<>();
}