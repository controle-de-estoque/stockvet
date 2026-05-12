package com.finconnect.auth_service.exception_handler;

import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.finconnect.auth_service.exception_handler.exceptions.PetNameIsIncorrectException;
import com.finconnect.auth_service.exception_handler.exceptions.UserAlredyExistsException;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleInternalServerError(Exception ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
            new Date(),
            ex.getMessage(),
            request.getDescription(false)
        );

        return ResponseEntity.internalServerError().body(response);
    }

    @ExceptionHandler(UserAlredyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleUserAlredyExistsException(Exception ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
            new Date(),
            ex.getMessage(),
            request.getDescription(false)
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(PetNameIsIncorrectException.class)
    public ResponseEntity<ExceptionResponse> handlePetNameIsIncorrectException(Exception ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
            new Date(),
            ex.getMessage(),
            request.getDescription(false)
        );

        return ResponseEntity.badRequest().body(response);
    }
}
