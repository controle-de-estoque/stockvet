package ufrpe.stockvet.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "item_movimentacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemMovimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "qtd_movimentada", nullable = false)
    private Integer qtdMovimentada;

    @ManyToOne
    @JoinColumn(name = "movimentacao_id", nullable = false)
    private Movimentacao movimentacao;

    @ManyToOne
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;
}