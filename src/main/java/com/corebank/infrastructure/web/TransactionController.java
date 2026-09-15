package com.corebank.infrastructure.web;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.corebank.application.command.AuthorizeTransactionUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Transaction Authorization Endpoints")
public class TransactionController {

    private final AuthorizeTransactionUseCase useCase;

    public TransactionController(AuthorizeTransactionUseCase useCase) {
        this.useCase = useCase;
    }

    @Operation(summary = "Authorize a new transaction (debit)", description = "Validates account balance and idempotency, then authorizes a transaction.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction authorized successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "422", description = "Transaction rejected (e.g., insufficient funds)")
    })
    @PostMapping("/authorize")
    public ResponseEntity<?> authorize(
            @Parameter(description = "Authenticated Account ID", required = true)
            @RequestHeader("X-Account-Id") UUID accountId,
            @RequestBody TransactionRequest request) {
        var command = new AuthorizeTransactionUseCase.AuthorizeTransactionCommand(accountId, request.amount(), request.type());
        var result = useCase.execute(command);
        return ResponseEntity.ok(result);
    }

    public record TransactionRequest(BigDecimal amount, String type) {}
}
