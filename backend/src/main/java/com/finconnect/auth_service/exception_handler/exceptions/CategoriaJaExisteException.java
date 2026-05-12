package com.finconnect.auth_service.exception_handler.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class CategoriaJaExisteException extends RuntimeException {

    public CategoriaJaExisteException(String message) {
        super(message);
    }

    CategoriaJaExisteException() {
        super("Já existe uma categoria com esse nome neste estoque");
    }
}