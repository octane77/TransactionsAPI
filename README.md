SEERBIT CODING INTERVIEW CHALLENGE


This repository contains a solution to the Seerbit Coding Interview Challenge.
The challenge is to create a RESTful API for calculating real-time statistics for the last 30 seconds of transactions.

Requirements
These are the additional requirements for the solution:

- Use Java to complete the challenge.
- Unit tests are compulsory.
- The API has to be thread-safe with concurrent requests.
- The POST /transactions and GET /statistics endpoints MUST execute in constant time and memory ie O(1). Scheduled cleanup is not sufficient.
- The solution has to work without a database (this also applies to in-memory databases).
- mvn clean install and mvn clean integration-test must complete successfully.
- In addition to passing the tests, source code should be well-commented. 
  The solution must be at a quality level that you would be comfortable enough to put in production.

Problem Statement
We would like to have a RESTful API for our transaction statistics. The main use case for the API is to calculate real-time statistics for the last 30 seconds of transactions. The API needs the following endpoints:

POST /transaction: called every time a transaction is made.
GET /transaction: returns the statistic based on the transactions of the last 30 seconds.
DELETE /transaction: deletes all transactions.

Specs
POST /transactions
This endpoint is called to create a new transaction. It MUST execute in constant time and memory (O(1)).

Body:
{
    "amount": "12.3343",
    "timestamp": "2018-07-17T09:59:51.312Z"
}

Where:

amount: transaction amount; a string of arbitrary length that is parsable as a BigDecimal.
timestamp: transaction time in the ISO 8601 format YYYY-MM-DDThh:mm:ss.sssZ in the UTC timezone (this is not the current timestamp).
Returns: Empty body with one of the following:

201: in case of success.
204: if the transaction is older than 30 seconds.
400: if the JSON is invalid.
422: if any of the fields are not parsable or the transaction date is in the future.

GET /statistics
This endpoint returns the statistics based on the transactions that happened in the last 30 seconds.
It MUST execute in constant time and memory (O(1)).

Returns:

{
    "sum": "1000.00",
    "avg": "100.53",
    "max": "200000.49",
    "min": "50.23",
    "count": 10
}

Where:

sum: a BigDecimal specifying the total sum of transaction value in the last 30 seconds.
avg: a BigDecimal specifying the average amount of transaction value in the last 30 seconds.
max: a BigDecimal specifying single highest transaction value in the last 30 seconds.
min: a BigDecimal specifying single lowest transaction value in the last 30 seconds.
count: a long specifying the total number of transactions that happened in the last 30 seconds.
All BigDecimal values always contain exactly two decimal places and use HALF_ROUND_UP rounding. For example, 10.345 is returned as 10.35, 10.8 is returned as 10.80.

DELETE /transactions
This endpoint causes all existing transactions to be deleted. The endpoint should accept an empty request body and return a 204 status code.


This solution follows the popular controller-service-repository pattern
The following is an explanation of all three layers, their capabilities, and the engineering decisions behind them

Starting with a bottom up approach we have the TransactionRepository class

The TransactionRepository class represents a repository for storing and managing transaction data for our TransactionsAPI. 
Here are the engineering choices made in this class:

Use of Concurrency and Atomic Variables
The transactions field is a ConcurrentNavigableMap, which allows for efficient concurrent access to the data structure, ensuring thread-safety. 
Similarly, the transactionCount, runningTotalSum, runningTotalMax, and runningTotalMin fields are all AtomicInteger or AtomicReference objects, which ensures that all operations on these fields are atomic and thread-safe.

Handling Invalid Transactions
The addTransaction() method checks for various conditions that may render a transaction invalid. If a transaction is invalid, an exception is thrown. This approach ensures that invalid data is not stored in the repository.

Use of BigDecimal
All currency-related calculations are done using BigDecimal, which is a fixed-point decimal type that is suitable for representing currency values with high precision. The use of BigDecimal ensures that there are no rounding errors in the calculations.

Transaction Expiration
The TRANSACTION_EXPIRATION_SECONDS constant is set to 30 seconds, which means that transactions that are older than 30 seconds are considered expired and are not included in the statistical calculations.

Statistics Calculation
The getStatistics() method calculates statistics (sum, average, max, min, count) for the transactions in the repository. The calculations are based on the transactions that are not expired, and are done using the ConcurrentNavigableMap data structure and the AtomicReference fields. This ensures that the statistics are calculated efficiently and accurately.

Deletion of All Transactions
The deleteAllTransactions() method deletes all transactions from the repository by clearing the transactions data structure and resetting the AtomicInteger and AtomicReference fields.

Logging
The class uses the Logger and LoggerFactory classes from the slf4j library to log debug and error messages. This helps with debugging and troubleshooting the application.


Summary Engineering Decisions:

The TransactionService class is responsible for adding new transactions, retrieving statistics about existing transactions, and deleting all transactions from the repository. It performs validations on the transaction data before adding it to the repository and throws exceptions if the data is invalid. The service delegates the actual storage and retrieval of transactions to the TransactionRepository.

Engineering decisions made in this class include:

Use of a Logger: The class uses a logger to output logs to aid in debugging and tracking issues that may occur during runtime. This decision was made to enable efficient debugging and easy tracking of errors.

Use of Constructor Injection: The class uses constructor injection to inject an instance of the TransactionRepository into the TransactionService class. This decision was made to enable easy unit testing and to ensure that the TransactionRepository can be easily swapped out with another implementation if required.

Validation of Transactions: The class validates each transaction before adding it to the repository. Transactions are only added if they are not older than 30 seconds and not in the future. This decision was made to ensure that only valid transactions are stored in the repository.

Handling of Invalid Transactions: If an invalid transaction is encountered, the class throws an InvalidTransactionException with an appropriate error code. This decision was made to ensure that the caller is aware of the issue and can take appropriate action.

Use of Try-Catch Blocks: The class uses try-catch blocks to catch exceptions that may be thrown during the processing of a transaction. This decision was made to ensure that the class does not crash if an exception occurs and to handle the exception appropriately.

Separation of Concerns: The class separates the logic for adding transactions, getting statistics, and deleting transactions into separate methods. This decision was made to ensure that the class has a single responsibility and is easy to understand and maintain.

Error Handling: The class logs errors when they occur and throws a runtime exception if an error occurs when getting statistics. This decision was made to ensure that errors are logged and can be easily tracked and fixed.



The TransactionRepository class is responsible for storing and retrieving transaction information, as well as calculating statistics for a given time period.

Engineering decisions made in this class include:

Using a ConcurrentNavigableMap to store transactions, allowing for efficient lookups and updates.

Using atomic variables to ensure thread-safety when accessing and updating shared state.

Rounding all computed statistics to two decimal places using RoundingMode.HALF_UP.

Storing the maximum and minimum transaction amounts using AtomicReference variables, initialized to BigDecimal.ZERO and null, respectively. This allows for easier updating when new transactions are added, while ensuring that non-null minimum values are returned by the getStatistics() method.

Storing the timestamp of each transaction in milliseconds, rather than as an Instant object. This reduces storage requirements and simplifies the calculation of recent transactions.

Checking for null transactions and amounts to avoid NullPointerExceptions.

Using SLF4J logging to log errors and debug messages.

Using an enum class ErrorCode to represent error codes for use in InvalidTransactionException instead of just using strings.

These decisions were made to ensure thread-safety, efficiency, and clarity in the implementation of the class.
