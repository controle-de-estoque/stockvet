package ufrpe.stockvet.DTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record RespostaMovimentacaoDTO(
        UUID id,
        String tipo,
        LocalDate data,
        LocalTime hora,
        String justificativa,
        UUID procedimentoId,
        List<RespostaItemMovimentacaoDTO> itens
) {}
