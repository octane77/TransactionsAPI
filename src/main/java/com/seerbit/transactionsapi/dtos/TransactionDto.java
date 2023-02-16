package com.seerbit.transactionsapi.dtos;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionDto {
    // amount field is required and can not be null
    @NotNull
    private BigDecimal amount;

    // timestamp field is required and can not be null
    @NotNull
    private Instant timestamp;

    public TransactionDto() {
    }

    // Constructor to initialize fields
    public TransactionDto(@NotNull BigDecimal amount, @NotNull Instant timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    // Getter for amount field
    public BigDecimal getAmount() {
        return amount;
    }

    // Setter for amount field
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    // Getter for timestamp field
    public Instant getTimestamp() {
        return timestamp;
    }

    // Setter for timestamp field
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TransactionDto{" +
                "amount=" + amount +
                ", timestamp=" + timestamp +
                '}';
    }
}
