package com.finconnect.auth_service.dto;

import java.util.UUID;

public record ProcedimentoRequest(
    UUID estoque,
    String nomeProcedimento,
    String nomeEspecie,
    Genero genero
) {}