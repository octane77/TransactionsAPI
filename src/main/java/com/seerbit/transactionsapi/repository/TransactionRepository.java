package com.seerbit.transactionsapi.repository;

import com.seerbit.transactionsapi.exceptions.ErrorCode;
import com.seerbit.transactionsapi.exceptions.InvalidTransactionException;
import com.seerbit.transactionsapi.model.Statistics;
import com.seerbit.transactionsapi.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class TransactionRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionRepository.class);
    private static final long TRANSACTION_EXPIRATION_SECONDS = 30;
    private final ConcurrentNavigableMap<Long, Transaction> transactions = new ConcurrentSkipListMap<>();
    private final AtomicInteger transactionCount = new AtomicInteger();
    private final AtomicReference<BigDecimal> runningTotalSum = new AtomicReference<>(BigDecimal.ZERO);
    private final AtomicReference<BigDecimal> runningTotalMax = new AtomicReference<>(BigDecimal.ZERO);
    private final AtomicReference<BigDecimal> runningTotalMin = new AtomicReference<>(null); // changed from ZERO to null

    public void addTransaction(Transaction transaction) throws InvalidTransactionException {
        Instant now = Instant.now();
        Instant transactionTime = Objects.requireNonNull(transaction, "Transaction must not be null").getTimestamp();

        if (transactionTime.isAfter(now)) {
            LOGGER.error("Transaction date is in the future: {}", transaction);
            throw new InvalidTransactionException(ErrorCode.TRANSACTION_DATE_IN_FUTURE);
        }

        if (now.minusSeconds(TRANSACTION_EXPIRATION_SECONDS).isAfter(transactionTime)) {
            LOGGER.error("Transaction is older than 30 seconds: {}", transaction);
            throw new InvalidTransactionException(ErrorCode.TRANSACTION_IS_OLDER_THAN_30_SECONDS);
        }

        if (transaction.getAmount().scale() > 2) {
            LOGGER.error("Transaction fields are not parsable: {}", transaction);
            throw new InvalidTransactionException(ErrorCode.TRANSACTION_FIELDS_NOT_PARSABLE);
        }

        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            LOGGER.error("Transaction amount is not positive: {}", transaction);
            throw new InvalidTransactionException(ErrorCode.TRANSACTION_JSON_INVALID);
        }

        transactions.put(transactionTime.toEpochMilli(), transaction);
        transactionCount.incrementAndGet();
        runningTotalSum.getAndUpdate(sum -> sum.add(transaction.getAmount()));
        runningTotalMax.getAndUpdate(max -> transaction.getAmount().max(max));
        runningTotalMin.getAndUpdate(min -> {
            if (min == null) {
                return transaction.getAmount(); // set the first transaction amount as the minimum
            } else {
                return transaction.getAmount().min(min).setScale(2, RoundingMode.HALF_UP); // otherwise, update with the minimum value
            }
        });
        LOGGER.debug("Added transaction: {}", transaction);
    }

    public Statistics getStatistics() {
        if (transactionCount.get() == 0) {
            return new Statistics(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0);
        } else {
            Instant now = Instant.now();
            Instant thirtySecondsAgo = now.minusSeconds(TRANSACTION_EXPIRATION_SECONDS);

            ConcurrentNavigableMap<Long, Transaction> recentTransactions = transactions.headMap(now.toEpochMilli(), true).tailMap(thirtySecondsAgo.toEpochMilli(), true);

            BigDecimal sum = runningTotalSum.get();
            BigDecimal max = runningTotalMax.get();
            BigDecimal min = runningTotalMin.get();
            int count = transactionCount.get();

            BigDecimal average = sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);

            LOGGER.debug("Calculated statistics: sum={}, avg={}, max={}, min={}, count={}", sum, average, max, min, count);

            return new Statistics(sum, average, max, min, count);
        }
    }

    public void deleteAllTransactions() {
        LOGGER.debug("Deleting all transactions");
        transactions.clear();
        transactionCount.set(0);
        runningTotalSum.set(BigDecimal.ZERO);
        runningTotalMax.set(BigDecimal.ZERO);
        runningTotalMin.set(null);
    }

    public int getTransactionCount() {
        return transactionCount.get();
    }

    public boolean isEmpty() {
        return transactionCount.get() == 0;
    }
}