package com.finconnect.auth_service.exception_handler.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PetNameIsIncorrectException extends RuntimeException{

    PetNameIsIncorrectException() {
        super("Nome do pet está incorreto");
    }

    public PetNameIsIncorrectException(String message) {
        super(message);
    }
}
