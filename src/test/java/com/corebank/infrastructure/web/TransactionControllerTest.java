package com.corebank.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.corebank.application.command.AuthorizeTransactionUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthorizeTransactionUseCase authorizeTransactionUseCase;

    @Test
    @DisplayName("Should authorize a debit transaction using X-Account-Id header")
    void shouldAuthorizeTransactionUsingHeader() throws Exception {
        UUID accountId = UUID.randomUUID();
        var response = new AuthorizeTransactionUseCase.AuthorizeTransactionResult(
                UUID.randomUUID(), "AUTHORIZED", "Transaction successful");

        when(authorizeTransactionUseCase.execute(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/transactions/authorize")
                .header("X-Account-Id", accountId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":250.00,\"type\":\"DEBIT\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Transaction successful"));
    }

    @Test
    @DisplayName("Should reject invalid transaction payload")
    void shouldRejectInvalidTransactionPayload() throws Exception {
        mockMvc.perform(post("/api/v1/transactions/authorize")
                .header("X-Account-Id", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":-10.00,\"type\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}