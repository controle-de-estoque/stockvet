package ufrpe.stockvet.DTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class EntradaDTO {
    private UUID produtoId;
    private String marca;
    private LocalDate dataValidade;
    private Integer quantidade;
    private String justificativa;
}
