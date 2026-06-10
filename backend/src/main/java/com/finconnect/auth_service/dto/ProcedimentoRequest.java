package com.finconnect.auth_service.dto;

import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ProcedimentoRequest(
    @NotNull(message = "O ID do estoque é obrigatório")
    UUID estoque,

    @NotBlank(message = "O nome do procedimento é obrigatório")
    String nomeProcedimento,

    @NotBlank(message = "A espécie é obrigatória")
    String nomeEspecie,

    @NotNull(message = "O gênero é obrigatório")
    Genero genero,

    @NotEmpty(message = "O procedimento deve conter pelo menos um produto")
    @Valid
    List<ItemProcedimentoRequest> itens
) {
    // Record auxiliar para receber as propriedades de cada produto enviado pelo Angular
    public record ItemProcedimentoRequest(
        @NotNull(message = "O ID do produto é obrigatório")
        UUID produtoId,

        @NotNull(message = "A quantidade é obrigatória")
        Double quantidade,

        @NotBlank(message = "O tipo de consumo é obrigatório")
        String tipo
    ) {}
}