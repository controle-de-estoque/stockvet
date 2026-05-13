package com.finconnect.auth_service.dto;

import java.util.UUID;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCessionarioRequest(

    @NotBlank
    String nome,

    @NotBlank
    @Email
    String email,

    @NotNull
    UUID estoque
) {}