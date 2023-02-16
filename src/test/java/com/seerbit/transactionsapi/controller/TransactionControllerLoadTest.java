package com.seerbit.transactionsapi.controller;

import com.seerbit.transactionsapi.dtos.TransactionDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TransactionControllerLoadTest {
    @Autowired
    private WebTestClient webClient;

    @Test
    void testConcurrentRequests() throws InterruptedException {
        // Number of concurrent requests to simulate
        int numRequests = 100;
        // Number of transactions to add per request
        int numTransactions = 10;

        // Send the requests and measure the response time
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numRequests; i++) {
            for (int j = 1; j <= numTransactions; j++) {
                TransactionDto transactionDto = new TransactionDto(BigDecimal.valueOf(j), Instant.now());
                webClient.post().uri("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(transactionDto))
                        .exchange()
                        .expectStatus().isEqualTo(HttpStatus.CREATED)
                        .returnResult(Void.class);
            }
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        // Calculate the expected response time for a single request
        long expectedTime = elapsedTime / numRequests;

        // Send the requests in parallel
        Flux.range(1, numRequests)
                .parallel()
                .runOn(Schedulers.fromExecutor(Executors.newFixedThreadPool(4)))
                .doOnNext(i -> {
                    for (int j = 1; j <= numTransactions; j++) {
                        TransactionDto transactionDto = new TransactionDto(BigDecimal.valueOf(j), Instant.now());
                        webClient.post().uri("/api/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromValue(transactionDto))
                                .exchange()
                                .expectStatus().isEqualTo(HttpStatus.CREATED)
                                .returnResult(Void.class);
                    }
                })
                .sequential()
                .blockLast();

        // Wait for all transactions to be processed
        TimeUnit.SECONDS.sleep(2);

        // Check that the response time is within an acceptable range
        long actualTime = System.currentTimeMillis() - startTime;
        long maxTime = expectedTime * 5;
        assertFalse(actualTime <= maxTime, "Response time too slow: " + actualTime + " ms");
    }
}