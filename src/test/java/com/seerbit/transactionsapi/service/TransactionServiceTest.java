package com.seerbit.transactionsapi.service;

import com.seerbit.transactionsapi.dtos.TransactionDto;
import com.seerbit.transactionsapi.exceptions.InvalidTransactionException;
import com.seerbit.transactionsapi.model.Statistics;
import com.seerbit.transactionsapi.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    private TransactionRepository mockRepository;
    private TransactionService transactionService;

    @BeforeEach
    public void setup() {
        // create a mock transaction repository
        mockRepository = mock(TransactionRepository.class);
        // create a transaction service using the mock repository
        transactionService = new TransactionService(mockRepository);
    }

    @Test
    public void testAddTransactionWithValidInput() throws InvalidTransactionException {
        // create a valid transaction DTO
        TransactionDto transactionDto = new TransactionDto(BigDecimal.valueOf(100.0), Instant.now());
        // add the transaction using the transaction service
        transactionService.addTransaction(transactionDto);
        // verify that the mock repository's addTransaction() method was called once with any argument
        verify(mockRepository, times(1)).addTransaction(any());
    }

    @Test
    public void testAddTransactionWithFutureDate() throws InvalidTransactionException {
        // create a transaction DTO with a future timestamp
        TransactionDto transactionDto = new TransactionDto(BigDecimal.valueOf(100.0), Instant.now().plusSeconds(60));
        // check that adding the transaction using the transaction service throws an InvalidTransactionException
        assertThrows(InvalidTransactionException.class, () -> transactionService.addTransaction(transactionDto));
        // verify that the mock repository's addTransaction() method was not called
        verify(mockRepository, times(0)).addTransaction(any());
    }

    @Test
    public void testAddTransactionWithOlderThan30Seconds() throws InvalidTransactionException {
        // create a transaction DTO with a timestamp that is more than 30 seconds old
        TransactionDto transactionDto = new TransactionDto(BigDecimal.valueOf(100.0), Instant.now().minusSeconds(31));
        // check that adding the transaction using the transaction service throws an InvalidTransactionException
        assertThrows(InvalidTransactionException.class, () -> transactionService.addTransaction(transactionDto));
        // verify that the mock repository's addTransaction() method was not called
        verify(mockRepository, times(0)).addTransaction(any());
    }

    @Test
    public void testGetStatistics() {
        // create a mock statistics object
        Statistics mockStatistics = new Statistics(BigDecimal.valueOf(100.0), BigDecimal.valueOf(50.0), BigDecimal.valueOf(200.0), BigDecimal.valueOf(5), 1L);
        // when the mock repository's getStatistics() method is called, return the mock statistics object
        when(mockRepository.getStatistics()).thenReturn(mockStatistics);
        // call the transaction service's getStatistics() method
        Statistics result = transactionService.getStatistics();
        // verify that the mock repository's getStatistics() method was called once
        verify(mockRepository, times(1)).getStatistics();
        // verify that the result matches the mock statistics object
        assertEquals(mockStatistics, result);
    }

    @Test
    public void testGetStatisticsWithError() {
        // when the mock repository's getStatistics() method is called, throw a runtime exception
        when(mockRepository.getStatistics()).thenThrow(new RuntimeException());
        // check that calling the transaction service's getStatistics() method throws a runtime exception
        assertThrows(RuntimeException.class, () -> transactionService.getStatistics());
        // verify that the mock repository's getStatistics() method was called once
        verify(mockRepository, times(1)).getStatistics();
    }

    @Test
    public void testDeleteAllTransactions() {
        // call the transaction service's deleteAllTransactions() method
        transactionService.deleteAllTransactions();
        // verify that the mock repository's deleteAllTransactions() method was called once
        verify(mockRepository, times(1)).deleteAllTransactions();
    }
}
