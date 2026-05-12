package com.finconnect.auth_service.exception_handler.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ProdutoJaExisteException extends RuntimeException {

    public ProdutoJaExisteException(String message) {
        super(message);
    }
}