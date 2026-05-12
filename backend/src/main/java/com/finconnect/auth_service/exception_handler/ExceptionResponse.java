package com.finconnect.auth_service.exception_handler;

import java.util.Date;

public record ExceptionResponse(
    Date date,
    String message,
    String details
) {}
