package com.seerbit.transactionsapi.controller;

// Import statements for necessary classes

import com.seerbit.transactionsapi.dtos.TransactionDto;
import com.seerbit.transactionsapi.exceptions.InvalidTransactionException;
import com.seerbit.transactionsapi.model.Statistics;
import com.seerbit.transactionsapi.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Indicates that the test class will use Mockito's JUnit 5 extension for setting up mock objects
@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {
    // A mock object for the TransactionService class
    @Mock
    private TransactionService transactionService;

    // The object under test that will be injected with the mock TransactionService object
    @InjectMocks
    private TransactionController transactionController;

    // A TransactionDto object that will be used in the tests
    private TransactionDto transactionDto;

    @BeforeEach
    public void setUp() {
        // Initialize the TransactionDto object with some data for testing
        transactionDto = new TransactionDto(BigDecimal.TEN, Instant.now());
    }

    // Test the addTransaction() method when the transactionDto object is valid
    @Test
    public void testAddTransactionValid() throws InvalidTransactionException {
        // Call the addTransaction() method of the TransactionController with the TransactionDto object
        ResponseEntity<Void> responseEntity = transactionController.addTransaction(transactionDto);

        // Assert that the response code is 201 CREATED
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        // Verify that the addTransaction() method of the TransactionService was called with the TransactionDto object
        verify(transactionService, times(1)).addTransaction(any(TransactionDto.class));
    }

    // Test the getStatistics() method
    @Test
    @DisplayName("Test getStatistics returns OK status code and correct statistics")
    public void testGetStatistics() {
        // Create a mock Statistics object to be returned by the getStatistics() method of the TransactionService
        Statistics statistics = new Statistics(BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, 1L);

        // Set up the mock TransactionService to return the mock Statistics object when getStatistics() is called
        when(transactionService.getStatistics()).thenReturn(statistics);

        // Call the getStatistics() method of the TransactionController
        ResponseEntity<Statistics> responseEntity = transactionController.getStatistics();

        // Assert that the response code is 200 OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert that the response body is the same as the mock Statistics object
        assertEquals(statistics, responseEntity.getBody());

        // Verify that the getStatistics() method of the TransactionService was called
        verify(transactionService, times(1)).getStatistics();
    }

    // Test the deleteTransactions() method
    @Test
    @DisplayName("Test deleteTransactions returns NO_CONTENT status code")
    public void testDeleteTransactions() {
        // Call the deleteTransactions() method of the TransactionController
        ResponseEntity<Void> responseEntity = transactionController.deleteTransactions();

        // Assert that the response code is 204 NO CONTENT
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        // Verify that the deleteAllTransactions() method of the TransactionService was called
        verify(transactionService, times(1)).deleteAllTransactions();
    }
}