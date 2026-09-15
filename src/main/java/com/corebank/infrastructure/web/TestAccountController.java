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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/api/v1/test/accounts")
@Profile({"dev", "test"})
public class TestAccountController {

    private final CreateAccountUseCase createAccountUseCase;

    public TestAccountController(CreateAccountUseCase createAccountUseCase) {
        this.createAccountUseCase = createAccountUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateAccountUseCase.CreateAccountResult> createTestAccount(@RequestBody @Valid CreateTestAccountRequest request) {
        CreateAccountUseCase.CreateAccountCommand command = new CreateAccountUseCase.CreateAccountCommand(
                request.accountId(),
                request.initialBalance()
        );
        CreateAccountUseCase.CreateAccountResult result = createAccountUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    public record CreateTestAccountRequest(
            @NotNull(message = "accountId is required") UUID accountId, 
            @NotNull(message = "initialBalance is required") @PositiveOrZero(message = "initialBalance must be positive or zero") BigDecimal initialBalance
    ) {}
}
