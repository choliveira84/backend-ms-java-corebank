package com.corebank.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
import com.corebank.domain.transaction.TransactionType;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
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

            verify(authorizeTransactionUseCase).execute(
                new AuthorizeTransactionUseCase.AuthorizeTransactionCommand(accountId,
                    new BigDecimal("250.00"), TransactionType.DEBIT));
            }

            @Test
            @DisplayName("Should authorize a PIX transaction")
            void shouldAuthorizePixTransaction() throws Exception {
            UUID accountId = UUID.randomUUID();
            when(authorizeTransactionUseCase.execute(any())).thenReturn(
                new AuthorizeTransactionUseCase.AuthorizeTransactionResult(
                    UUID.randomUUID(), "AUTHORIZED", "Transaction successful"));

            mockMvc.perform(post("/api/v1/transactions/authorize")
                .header("X-Account-Id", accountId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":250.00,\"type\":\"PIX\"}"))
                .andExpect(status().isOk());

            verify(authorizeTransactionUseCase).execute(
                new AuthorizeTransactionUseCase.AuthorizeTransactionCommand(accountId,
                    new BigDecimal("250.00"), TransactionType.PIX));
            }

            @Test
            @DisplayName("Should reject an unknown transaction type before authorization")
            void shouldRejectUnknownTransactionType() throws Exception {
            mockMvc.perform(post("/api/v1/transactions/authorize")
                .header("X-Account-Id", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":250.00,\"type\":\"CASH\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"));

            verify(authorizeTransactionUseCase, never()).execute(any());
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

    @Test
    @DisplayName("Should reject case and whitespace variants of transaction type")
    void shouldRejectNonCanonicalTransactionTypes() throws Exception {
        for (String type : new String[] { "debit", "Transfer", " pix ", "" }) {
            mockMvc.perform(post("/api/v1/transactions/authorize")
                    .header("X-Account-Id", UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"amount\":250.00,\"type\":\"" + type + "\"}"))
                    .andExpect(status().isBadRequest());
        }

        verify(authorizeTransactionUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Should reject a missing transaction type")
    void shouldRejectMissingTransactionType() throws Exception {
        mockMvc.perform(post("/api/v1/transactions/authorize")
                .header("X-Account-Id", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\":250.00}"))
                .andExpect(status().isBadRequest());

        verify(authorizeTransactionUseCase, never()).execute(any());
    }
}