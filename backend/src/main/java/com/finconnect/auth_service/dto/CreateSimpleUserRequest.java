package com.finconnect.auth_service.dto;

import java.util.UUID;
import org.hibernate.validator.constraints.Length;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSimpleUserRequest(

    @NotBlank
    String firstName,

    @NotBlank
    String lastName,
    
    @NotBlank
    @Email
    String email,

    @NotBlank
    @Length(min = 8)
    String password,
    
    @NotBlank
    String firstPetName,

    @NotNull
    UUID estoque
) {}