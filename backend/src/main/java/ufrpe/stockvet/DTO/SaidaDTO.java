package ufrpe.stockvet.DTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class SaidaDTO {
    private UUID produtoId;
    private Integer quantidade;
    private String justificativa;
    private UUID procedimentoId;
}
