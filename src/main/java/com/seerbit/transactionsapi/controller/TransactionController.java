package com.seerbit.transactionsapi.controller;

// Import statements for necessary classes

import com.seerbit.transactionsapi.dtos.TransactionDto;
import com.seerbit.transactionsapi.model.Statistics;
import com.seerbit.transactionsapi.service.TransactionService;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
// Indicates that the class will serve as a REST controller and map incoming HTTP requests to handler methods
@RestController
// Indicates the base URL path that will be used for mapping requests
@RequestMapping("/api/")
// Marks the class as a source of @ExceptionHandler methods to handle exceptions thrown by handler methods
@RestControllerAdvice
public class TransactionController {
    // An instance of the TransactionService class that will be used to handle transaction-related operations
    private final TransactionService transactionService;

    // Constructor for creating a new TransactionController object with the given TransactionService object
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // A handler method for adding new transactions to the system
    @SneakyThrows
    // Indicates that the method requires a validation of the incoming request body using the @Valid annotation
    @PostMapping("/transactions")
    public ResponseEntity<Void> addTransaction(@Valid @RequestBody TransactionDto transactionDto) {
        log.info("Received transaction request: {}", transactionDto);
        // Calls the addTransaction() method of the TransactionService object with the transactionDto object
        transactionService.addTransaction(transactionDto);
        // Returns a 201 CREATED status code in the HTTP response
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // A handler method for retrieving the statistics of all transactions within the last 30 seconds
    @GetMapping("/statistics")
    public ResponseEntity<Statistics> getStatistics() {
        log.info("Received request for statistics");
        // Calls the getStatistics() method of the TransactionService object to retrieve the statistics
        Statistics statistics = transactionService.getStatistics();
        // Returns a 200 OK status code in the HTTP response along with the statistics object
        return ResponseEntity.ok(statistics);
    }

    // A handler method for deleting all transactions from the system
    @DeleteMapping("/transactions")
    public ResponseEntity<Void> deleteTransactions() {
        log.info("Received request to delete all transactions");
        // Calls the deleteAllTransactions() method of the TransactionService object to delete all transactions
        transactionService.deleteAllTransactions();
        // Returns a 204 NO CONTENT status code in the HTTP response
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}