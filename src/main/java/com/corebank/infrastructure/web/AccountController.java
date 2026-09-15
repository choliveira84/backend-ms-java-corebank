package com.corebank.infrastructure.web;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.corebank.application.query.GetBalanceQuery;
import com.corebank.domain.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Account Management and Inquiry Endpoints")
public class AccountController {

    private final GetBalanceQuery query;

    public AccountController(GetBalanceQuery query) {
        this.query = query;
    }

    @Operation(summary = "Get account balance", description = "Retrieves the real-time balance projection for the authenticated account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balance retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(
            @Parameter(description = "Authenticated Account ID", required = true)
            @RequestHeader("X-Account-Id") UUID accountId) {
        var projection = query.execute(accountId);
        if (projection.isEmpty()) {
            throw new ResourceNotFoundException("Account " + accountId + " not found");
        }
        return ResponseEntity.ok(projection.get());
    }
}
