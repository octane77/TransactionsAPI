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