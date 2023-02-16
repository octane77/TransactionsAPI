package com.seerbit.transactionsapi.exceptions;

public enum ErrorCode {
    TRANSACTION_IS_OLDER_THAN_30_SECONDS,
    TRANSACTION_JSON_INVALID,
    TRANSACTION_FIELDS_NOT_PARSABLE,
    TRANSACTION_DATE_IN_FUTURE,
}
