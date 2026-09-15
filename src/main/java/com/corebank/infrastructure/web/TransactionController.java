package com.corebank.infrastructure.web;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import com.corebank.application.command.AuthorizeTransactionUseCase;
import com.corebank.domain.transaction.TransactionType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Transaction Authorization Endpoints")
@Validated
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
    public ResponseEntity<AuthorizeTransactionUseCase.AuthorizeTransactionResult> authorize(
            @Parameter(description = "Authenticated Account ID", required = true)
            @RequestHeader("X-Account-Id") UUID accountId,
            @RequestBody @Valid TransactionRequest request) {
        var command = new AuthorizeTransactionUseCase.AuthorizeTransactionCommand(accountId, request.amount(), request.type());
        var result = useCase.execute(command);
        return ResponseEntity.ok(result);
    }

        public record TransactionRequest(
            @NotNull(message = "amount is required")
            @Positive(message = "amount must be greater than zero")
            BigDecimal amount,

                @NotNull(message = "type is required")
                @Schema(description = "Supported transaction type", example = "DEBIT")
                TransactionType type) {}
}
