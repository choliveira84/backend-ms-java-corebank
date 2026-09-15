package com.corebank.infrastructure.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.corebank.application.command.CreateAccountUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TestAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateAccountUseCase createAccountUseCase;

    @Test
    void createTestAccount_ShouldReturn201_WhenValidPayload() throws Exception {
        UUID accountId = UUID.randomUUID();
        BigDecimal balance = new BigDecimal("1000.00");

        TestAccountController.CreateTestAccountRequest request = new TestAccountController.CreateTestAccountRequest(accountId, balance);
        CreateAccountUseCase.CreateAccountResult result = new CreateAccountUseCase.CreateAccountResult(accountId, balance, "CREATED");

        when(createAccountUseCase.execute(any())).thenReturn(result);

        // Warmup MockMvc dispatcher
        mockMvc.perform(post("/api/v1/test/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        org.junit.jupiter.api.Assertions.assertTimeout(java.time.Duration.ofMillis(200), () -> {
            mockMvc.perform(post("/api/v1/test/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                    .andExpect(jsonPath("$.balance").value(balance.doubleValue()))
                    .andExpect(jsonPath("$.status").value("CREATED"));
        });
    }
}
