package com.seerbit.transactionsapi.controller;

import com.seerbit.transactionsapi.exceptions.ErrorCode;
import com.seerbit.transactionsapi.exceptions.InvalidTransactionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
// Indicates that this class provides global exception handling for controllers.
public class TransactionControllerAdvice {

    @ExceptionHandler(InvalidTransactionException.class)
    // Marks this method to handle the specified exception type.
    public ResponseEntity<Void> handleInvalidTransactionException(InvalidTransactionException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        // Retrieves the error code from the exception.
        HttpStatus httpStatus = switch (errorCode) {
            // Selects an HTTP status based on the error code.
            case TRANSACTION_JSON_INVALID -> HttpStatus.BAD_REQUEST;
            case TRANSACTION_IS_OLDER_THAN_30_SECONDS -> HttpStatus.NO_CONTENT;
            case TRANSACTION_DATE_IN_FUTURE, TRANSACTION_FIELDS_NOT_PARSABLE -> HttpStatus.UNPROCESSABLE_ENTITY;
        };
        log.error("Invalid transaction exception occurred with error code {}", errorCode);
        return ResponseEntity.status(httpStatus).build();
        // Returns a response entity with the selected HTTP status.
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Void> handleHttpMessageNotReadableException() {
        log.error("HTTP message not readable exception occurred");
        return ResponseEntity.badRequest().build();
        // Returns a response entity with a 400 Bad Request HTTP status.
    }
}
