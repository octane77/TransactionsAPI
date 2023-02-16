package com.seerbit.transactionsapi.model;

import java.math.BigDecimal;
import java.time.Instant;

/* This class has two fields:
    Amount, which is a BigDecimal representing the transaction amount,
    Timestamp, which is an Instant representing the time the transaction occurred.
    It also has a constructor and getter methods for these fields.
 */
public class Transaction {
    private BigDecimal amount;
    private Instant timestamp;

    public Transaction(BigDecimal amount, Instant timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}