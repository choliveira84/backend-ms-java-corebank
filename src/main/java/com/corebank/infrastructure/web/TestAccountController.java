package com.corebank.infrastructure.web;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.corebank.application.command.CreateAccountUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Tag(name = "Test Accounts", description = "Endpoints for managing accounts in non-production environments")
@RestController
@RequestMapping("/api/v1/test/accounts")
@Profile({ "dev", "test" })
public class TestAccountController {

    private final CreateAccountUseCase createAccountUseCase;

    public TestAccountController(CreateAccountUseCase createAccountUseCase) {
        this.createAccountUseCase = createAccountUseCase;
    }

    @Operation(summary = "Create a test account", description = "Creates a new test account with an initial balance. Only available in dev and test profiles.")
    @ApiResponse(responseCode = "201", description = "Test account successfully created", content = @Content(schema = @Schema(implementation = CreateAccountUseCase.CreateAccountResult.class)))
    @PostMapping
    public ResponseEntity<CreateAccountUseCase.CreateAccountResult> createTestAccount(
            @RequestBody @Valid CreateTestAccountRequest request) {
        CreateAccountUseCase.CreateAccountCommand command = new CreateAccountUseCase.CreateAccountCommand(
                request.accountId(),
                request.initialBalance());
        CreateAccountUseCase.CreateAccountResult result = createAccountUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Schema(description = "Request payload for creating a test account")
    public record CreateTestAccountRequest(
            @Schema(description = "The unique identifier of the account", example = "550e8400-e29b-41d4-a716-446655440000") @NotNull(message = "accountId is required") UUID accountId,
            @Schema(description = "The initial balance of the account", example = "1000.00") @NotNull(message = "initialBalance is required") @PositiveOrZero(message = "initialBalance must be positive or zero") BigDecimal initialBalance) {
    }
}
