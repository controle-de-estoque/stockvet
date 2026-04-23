package ufrpe.stockvet.DTO;

import java.time.LocalDate;
import java.util.UUID;

public record RespostaItemMovimentacaoDTO(
        UUID id,
        UUID loteId,
        String marcaLote,
        LocalDate dataValidadeLote,
        Integer qtdMovimentada
) {}
