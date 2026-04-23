package ufrpe.stockvet.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "item_referencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemReferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "qtd_utilizada", nullable = false)
    private Double qtdUtilizada;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "procedimento_referencia_id", nullable = false)
    private ProcedimentoReferencia procedimentoReferencia;
}