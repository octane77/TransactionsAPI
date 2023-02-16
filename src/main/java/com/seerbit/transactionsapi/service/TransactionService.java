package com.seerbit.transactionsapi.service;

import com.seerbit.transactionsapi.dtos.TransactionDto;
import com.seerbit.transactionsapi.exceptions.ErrorCode;
import com.seerbit.transactionsapi.exceptions.InvalidTransactionException;
import com.seerbit.transactionsapi.model.Statistics;
import com.seerbit.transactionsapi.model.Transaction;
import com.seerbit.transactionsapi.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;

@Service
public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        log.info("TransactionService initialized");
    }

    public void addTransaction(TransactionDto transactionDto) throws InvalidTransactionException {
        Instant timestamp = transactionDto.getTimestamp();
        Instant current = Instant.now();
        if (timestamp.isAfter(current)) {
            log.error("Transaction date is in future: {}", transactionDto);
            throw new InvalidTransactionException(ErrorCode.TRANSACTION_DATE_IN_FUTURE);
        } else if (timestamp.isBefore(current.minusSeconds(30))) {
            log.error("Transaction is older than 30 seconds: {}", transactionDto);
            throw new InvalidTransactionException(ErrorCode.TRANSACTION_IS_OLDER_THAN_30_SECONDS);
        }
        try {
            Transaction transaction = new Transaction(transactionDto.getAmount(), transactionDto.getTimestamp());
            transactionRepository.addTransaction(transaction);
            log.info("Transaction added to repository: {}", transaction);
        } catch (NumberFormatException | HttpMessageNotReadableException exception) {
            log.error("Transaction fields not parsable: {}", transactionDto);
            throw new InvalidTransactionException(ErrorCode.TRANSACTION_FIELDS_NOT_PARSABLE);
        } catch (JsonParseException | DateTimeParseException exception) {
            log.error("Transaction JSON invalid: {}", transactionDto);
            throw new InvalidTransactionException(ErrorCode.TRANSACTION_JSON_INVALID);
        }
    }

    public Statistics getStatistics() {
        try {
            return transactionRepository.getStatistics();

        } catch (Exception e) {
            log.error("Error getting statistics", e);
            throw new RuntimeException("Error getting statistics", e);
        }
    }

    public void deleteAllTransactions() {
        transactionRepository.deleteAllTransactions();
        log.info("All transactions deleted from repository");
    }
}
