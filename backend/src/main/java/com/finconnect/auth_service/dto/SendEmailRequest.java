package com.finconnect.auth_service.dto;

public record SendEmailRequest(
    String destination,
    String subject,
    String message
) {}