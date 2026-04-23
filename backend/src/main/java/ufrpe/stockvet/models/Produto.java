package ufrpe.stockvet.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

// daqui para baixo perguntar sobre no diagrama n tem mas nos requisitos tem 😓
    @Column(nullable = false)
    private String categoria;


    @Column(nullable = false)
    private String tipo;


    @Column(name = "quantidade_critica", nullable = false)
    private Double quantidadeCritica;


    @Column(nullable = false)
    private Boolean ativo = true;


    @ManyToOne
    @JoinColumn(name = "unidade_medida_id", nullable = false)
    private UnidadeMedida unidadeMedida;
}