package com.corebank.infrastructure.web;

import com.corebank.application.command.AuthorizeTransactionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final AuthorizeTransactionUseCase useCase;

    public TransactionController(AuthorizeTransactionUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/authorize")
    public ResponseEntity<?> authorize(
            @RequestHeader("X-Account-Id") UUID accountId,
            @RequestBody TransactionRequest request) {
        try {
            var command = new AuthorizeTransactionUseCase.AuthorizeTransactionCommand(accountId, request.amount(), request.type());
            var result = useCase.execute(command);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of("status", "REJECTED", "error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    public record TransactionRequest(BigDecimal amount, String type) {}
}
