package com.finconnect.auth_service.dto;

import java.util.UUID;

public record SignInResponse(
    String jwt,
    UUID estoque
) {}
