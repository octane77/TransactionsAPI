package com.seerbit.transactionsapi.exceptions;

/*
I created an InvalidTransactionException class to handle exceptions when an invalid transaction is received.
This class is used to throw an exception when an invalid transaction is received, and catch it in the appropriate place to handle it.
*/
public class InvalidTransactionException extends Exception {
    private final ErrorCode errorCode;

    public InvalidTransactionException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
