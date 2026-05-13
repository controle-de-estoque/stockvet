package com.finconnect.auth_service.exception_handler.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InsufficientProductsException extends RuntimeException {

    public InsufficientProductsException() {
        super("Não há produtos suficiente no estoque para realizar a operação");
    }

    public InsufficientProductsException(String message) {
        super(message);
    }
}
