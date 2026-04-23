package ufrpe.stockvet.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "procedimento_referencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcedimentoReferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String especie;

    @Column(nullable = false)
    private String sexo;

    @Column(nullable = false)
    private Double peso;


}