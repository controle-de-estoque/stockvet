package com.finconnect.auth_service.exception_handler.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class LoteNotFoundException extends RuntimeException {
    
    public LoteNotFoundException() {
        super("Lote não encontrado");
    }

    public LoteNotFoundException(String message) {
        super(message);
    }
}
