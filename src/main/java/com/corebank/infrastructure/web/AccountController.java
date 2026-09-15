package com.corebank.infrastructure.web;

import com.corebank.application.query.GetBalanceQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final GetBalanceQuery query;

    public AccountController(GetBalanceQuery query) {
        this.query = query;
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(@RequestHeader("X-Account-Id") UUID accountId) {
        var projection = query.execute(accountId);
        if (projection.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "ACCOUNT_NOT_FOUND"));
        }
        return ResponseEntity.ok(projection.get());
    }
}
