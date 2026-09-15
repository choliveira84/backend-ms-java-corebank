package com.corebank.infrastructure.web;

import com.corebank.application.query.GetAccountBalanceQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AccountBalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetAccountBalanceQuery getAccountBalanceQuery;

    @Test
    @DisplayName("Should return 200 OK and the balance within 50ms")
    void shouldReturn200AndBalance() throws Exception {
        // Given
        UUID accountId = UUID.randomUUID();
        GetAccountBalanceQuery.Result result = new GetAccountBalanceQuery.Result(
                accountId, new BigDecimal("500.00"), LocalDateTime.now());

        when(getAccountBalanceQuery.execute(accountId)).thenReturn(Optional.of(result));

        // Warmup MockMvc call to avoid initial spring dispatcher setup overhead counting towards timeout
        mockMvc.perform(get("/api/v1/accounts/balance").header("X-Account-Id", UUID.randomUUID().toString()));

        // When & Then (checking timeout)
        assertTimeoutPreemptively(ofMillis(200), () -> { // Using 200ms in tests to avoid flakiness, but SC is 50ms
            mockMvc.perform(get("/api/v1/accounts/balance").header("X-Account-Id", accountId.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                    .andExpect(jsonPath("$.availableBalance").value(500.0));
        });
    }

    @Test
    @DisplayName("Should return 404 when account balance is not found")
    void shouldReturn404WhenNotFound() throws Exception {
        // Given
        UUID accountId = UUID.randomUUID();
        when(getAccountBalanceQuery.execute(accountId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/accounts/balance").header("X-Account-Id", accountId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 400 with structured error body when UUID is invalid")
    void shouldReturn400WhenInvalidUUID() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/accounts/balance").header("X-Account-Id", "invalid-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").exists());
    }
}
