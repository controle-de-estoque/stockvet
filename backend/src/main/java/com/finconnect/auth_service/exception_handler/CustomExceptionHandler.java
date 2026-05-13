package com.finconnect.auth_service.exception_handler;

import java.util.Date;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import com.finconnect.auth_service.exception_handler.exceptions.CategoriaJaExisteException;
import com.finconnect.auth_service.exception_handler.exceptions.InsufficientProductsException;
import com.finconnect.auth_service.exception_handler.exceptions.LoteNotFoundException;
import com.finconnect.auth_service.exception_handler.exceptions.PetNameIsIncorrectException;
import com.finconnect.auth_service.exception_handler.exceptions.ProdutoJaExisteException;
import com.finconnect.auth_service.exception_handler.exceptions.ResourceNotFoundException;
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

    @ExceptionHandler(CategoriaJaExisteException.class)
    public ResponseEntity<ExceptionResponse> handleCategoriaJaExisteException(Exception ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
            new Date(),
            ex.getMessage(),
            request.getDescription(false)
        );

        return ResponseEntity.status(409).body(response);
    }

    @ExceptionHandler(ProdutoJaExisteException.class)
    public ResponseEntity<ExceptionResponse> handleProdutoJaExisteException(Exception ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
            new Date(),
            ex.getMessage(),
            request.getDescription(false)
        );

        return ResponseEntity.status(409).body(response);
    }

    @ExceptionHandler(LoteNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleLoteNotFoundException(LoteNotFoundException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
            new Date(),
            ex.getMessage(),
            request.getDescription(false)
        );

        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(InsufficientProductsException.class)
    public ResponseEntity<ExceptionResponse> handleInsufficientProductsException(InsufficientProductsException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
            new Date(),
            ex.getMessage(),
            request.getDescription(false)
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
            new Date(),
            ex.getMessage(),
            request.getDescription(false)
        );

        return ResponseEntity.status(404).body(response);
    }
}
