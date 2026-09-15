package com.corebank.infrastructure.web;

import com.corebank.application.query.GetAccountBalanceQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Account management endpoints")
@Validated
public class AccountBalanceController {

    private final GetAccountBalanceQuery getAccountBalanceQuery;

    public AccountBalanceController(GetAccountBalanceQuery getAccountBalanceQuery) {
        this.getAccountBalanceQuery = getAccountBalanceQuery;
    }

        @GetMapping("/balance")
    @Operation(summary = "Get account balance", description = "Retrieves the current available balance for a specific account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balance retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GetAccountBalanceQuery.Result.class))),
            @ApiResponse(responseCode = "400", description = "Invalid account ID format", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found or balance not yet available", content = @Content)
    })
        public ResponseEntity<GetAccountBalanceQuery.Result> getBalance(@RequestHeader("X-Account-Id") UUID accountId) {
        return getAccountBalanceQuery.execute(accountId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
