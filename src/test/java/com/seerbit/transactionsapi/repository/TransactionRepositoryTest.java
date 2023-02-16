package com.seerbit.transactionsapi.repository;

import com.seerbit.transactionsapi.exceptions.ErrorCode;
import com.seerbit.transactionsapi.exceptions.InvalidTransactionException;
import com.seerbit.transactionsapi.model.Statistics;
import com.seerbit.transactionsapi.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TransactionRepositoryTest {
    private TransactionRepository transactionRepository;

    @BeforeEach
    public void setUp() {
        transactionRepository = new TransactionRepository();
    }

    /*
    The AddTransaction nested class contains four tests for the addTransaction method of the TransactionRepository. The first test adds a valid transaction and checks that the count of transactions in the repository is one. The second test creates a transaction with a future date and checks that the expected exception is thrown and the count of transactions is still zero. The third test creates a transaction with a timestamp that is older than 30 seconds and checks that the expected exception is thrown and the count of transactions is still zero. The fourth test creates a transaction with an amount that has more than two decimal places and checks that the expected exception is thrown and the count of transactions is still zero.
    */
    @Nested
    @DisplayName("Test addTransaction()")
    class AddTransaction {
        @Test
        public void testAddTransaction() throws InvalidTransactionException {
            Transaction transaction = new Transaction(new BigDecimal("50.75"), Instant.now());

            assertDoesNotThrow(() -> transactionRepository.addTransaction(transaction));
            assertEquals(1, transactionRepository.getTransactionCount());
        }

        @Test
        public void testAddTransaction_whenTransactionIsFutureDate() {
            Instant futureDate = Instant.now().plusSeconds(60);
            Transaction transaction = new Transaction(new BigDecimal("50.75"), futureDate);

            InvalidTransactionException exception = assertThrows(InvalidTransactionException.class, () -> transactionRepository.addTransaction(transaction));
            assertEquals(ErrorCode.TRANSACTION_DATE_IN_FUTURE, exception.getErrorCode());
            assertEquals(0, transactionRepository.getTransactionCount());
        }

        @Test
        public void testAddTransaction_whenTransactionIsOlderThan30Seconds() {
            Instant olderDate = Instant.now().minusSeconds(40);
            Transaction transaction = new Transaction(new BigDecimal("50.75"), olderDate);

            InvalidTransactionException exception = assertThrows(InvalidTransactionException.class, () -> transactionRepository.addTransaction(transaction));
            assertEquals(ErrorCode.TRANSACTION_IS_OLDER_THAN_30_SECONDS, exception.getErrorCode());
            assertEquals(0, transactionRepository.getTransactionCount());
        }

        @Test
        public void testAddTransaction_whenTransactionAmountHasMoreThan2DecimalPlaces() {
            Transaction transaction = new Transaction(new BigDecimal("50.759"), Instant.now());

            InvalidTransactionException exception = assertThrows(InvalidTransactionException.class, () -> transactionRepository.addTransaction(transaction));
            assertEquals(ErrorCode.TRANSACTION_FIELDS_NOT_PARSABLE, exception.getErrorCode());
            assertEquals(0, transactionRepository.getTransactionCount());
        }
    }

    @Nested
    @DisplayName("Test getStatistics()")
    class GetStatistics {
        @Test
        public void testGetStatistics_whenNoTransactions() {
            Statistics statistics = transactionRepository.getStatistics();

            assertNotNull(statistics);
            assertEquals(BigDecimal.ZERO, statistics.getSum());
            assertEquals(BigDecimal.ZERO, statistics.getAvg());
            assertEquals(BigDecimal.ZERO, statistics.getMax());
            assertEquals(BigDecimal.ZERO, statistics.getMin());
            assertEquals(0, statistics.getCount());
        }

        @Test
        public void testGetStatistics_whenTransactionsExist() throws InvalidTransactionException, InterruptedException {
            // Add three transactions to the repository
            Instant now = Instant.now();
            Transaction transaction1 = new Transaction(BigDecimal.valueOf(100.50), now.minusSeconds(10));
            transactionRepository.addTransaction(transaction1);
            Thread.sleep(1000); // Wait for 1 second to add the next transaction
            Transaction transaction2 = new Transaction(BigDecimal.valueOf(200.75), now.minusSeconds(5));
            transactionRepository.addTransaction(transaction2);
            Thread.sleep(1000); // Wait for 1 second to add the next transaction
            Transaction transaction3 = new Transaction(BigDecimal.valueOf(300.25), now);
            transactionRepository.addTransaction(transaction3);

            Statistics statistics = transactionRepository.getStatistics();

            assertEquals(BigDecimal.valueOf(601.50).setScale(2, RoundingMode.HALF_UP), statistics.getSum());
            assertEquals(BigDecimal.valueOf(200.50).setScale(2, RoundingMode.HALF_UP), statistics.getAvg());
            assertEquals(BigDecimal.valueOf(300.25).setScale(2, RoundingMode.HALF_UP), statistics.getMax());
            assertEquals(BigDecimal.valueOf(100.50).setScale(2, RoundingMode.HALF_UP), statistics.getMin());
            assertEquals(3, statistics.getCount());
        }


        /*
        The DeleteAllTransactions nested class contains two tests for the deleteAllTransactions method of the TransactionRepository. The first test checks that the method works correctly when there are no transactions in the repository. The second test adds a transaction to the repository, checks that the count of transactions is one and that the repository is not empty. It then calls the deleteAllTransactions method and checks that the count of transactions is zero and that the repository is empty.
        */
        @Nested
        @DisplayName("Test deleteAllTransactions()")
        class DeleteAllTransactions {
            @Test
            public void testDeleteAllTransactions_whenNoTransactions() {
                transactionRepository.deleteAllTransactions();

                assertEquals(0, transactionRepository.getTransactionCount());
                assertTrue(transactionRepository.isEmpty());
            }

            @Test
            public void testDeleteAllTransactions_whenTransactionsExist() throws InvalidTransactionException {
                BigDecimal amount = BigDecimal.valueOf(10.0);
                Transaction transaction = new Transaction(amount, Instant.now());
                transactionRepository.addTransaction(transaction);

                assertEquals(1, transactionRepository.getTransactionCount());
                assertFalse(transactionRepository.isEmpty());

                transactionRepository.deleteAllTransactions();

                assertEquals(0, transactionRepository.getTransactionCount());
                assertTrue(transactionRepository.isEmpty());
            }
        }
    }
}