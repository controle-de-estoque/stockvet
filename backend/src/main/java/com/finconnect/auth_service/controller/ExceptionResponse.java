package com.finconnect.auth_service.controller;

import java.util.Date;

public record ExceptionResponse(
    Date date,
    String message,
    String details
) {}
