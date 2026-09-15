package com.corebank.domain.account;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account_ledger")
public class AccountLedger {
    @Id
    private UUID id;
    private UUID accountId;
    private BigDecimal balance;
    @Version
    private Integer version;
    private LocalDateTime updatedAt;

    public AccountLedger() {}

    public AccountLedger(UUID id, UUID accountId, BigDecimal balance, Integer version, LocalDateTime updatedAt) {
        this.id = id;
        this.accountId = accountId;
        this.balance = balance;
        this.version = version;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAccountId() { return accountId; }
    public void setAccountId(UUID accountId) { this.accountId = accountId; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
