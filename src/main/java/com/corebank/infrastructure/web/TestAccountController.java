package com.corebank.infrastructure.web;

import com.corebank.application.command.CreateAccountUseCase;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/test/accounts")
@Profile({"dev", "test"})
public class TestAccountController {

    private final CreateAccountUseCase createAccountUseCase;

    public TestAccountController(CreateAccountUseCase createAccountUseCase) {
        this.createAccountUseCase = createAccountUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateAccountUseCase.CreateAccountResult> createTestAccount(@RequestBody CreateTestAccountRequest request) {
        CreateAccountUseCase.CreateAccountCommand command = new CreateAccountUseCase.CreateAccountCommand(
                request.accountId(),
                request.initialBalance()
        );
        CreateAccountUseCase.CreateAccountResult result = createAccountUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    public record CreateTestAccountRequest(UUID accountId, BigDecimal initialBalance) {}
}
