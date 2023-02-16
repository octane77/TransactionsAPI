package com.seerbit.transactionsapi.controller;

// Import statements for necessary classes

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seerbit.transactionsapi.dtos.TransactionDto;
import com.seerbit.transactionsapi.model.Statistics;
import com.seerbit.transactionsapi.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.Instant;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        // Delete all transactions before each test
        transactionService.deleteAllTransactions();
    }
    @Test
    public void testAddTransaction() throws Exception {
        // Create a TransactionDto object to send in the request body
        TransactionDto transactionDto = new TransactionDto(BigDecimal.TEN, Instant.now());

        // Send a POST request to the /transactions endpoint with the TransactionDto object as the request body
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDto)));

        // Assert that the response code is 201 CREATED
        resultActions.andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testGetStatistics() throws Exception {
        // Send a GET request to the /statistics endpoint
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics"));

        // Assert that the response code is 200 OK
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());

        // Assert that the response body contains the correct statistics
        Statistics statistics = transactionService.getStatistics();
        resultActions.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(statistics)));
    }

    @Test
    public void testDeleteTransactions() throws Exception {
        // Send a DELETE request to the /transactions endpoint
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/api/transactions"));

        // Assert that the response code is 204 NO CONTENT
        resultActions.andExpect(MockMvcResultMatchers.status().isNoContent());

        // Get the statistics after the transactions have been deleted
        ResultActions statisticsResultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics"));

        // Assert that the response code is 200 OK
        statisticsResultActions.andExpect(MockMvcResultMatchers.status().isOk());

        // Assert that the response body contains the correct statistics after the transactions have been deleted
        Statistics statistics = new Statistics(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0L);
        statisticsResultActions.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(statistics)));
    }
}
