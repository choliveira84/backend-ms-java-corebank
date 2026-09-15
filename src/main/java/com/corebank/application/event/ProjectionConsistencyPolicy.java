package com.corebank.application.event;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

public record ProjectionConsistencyPolicy(Duration maxDelay, Clock clock) {

    public ProjectionConsistencyPolicy {
        Objects.requireNonNull(maxDelay, "maxDelay must not be null");
        Objects.requireNonNull(clock, "clock must not be null");
        if (maxDelay.isNegative() || maxDelay.isZero()) {
            throw new IllegalArgumentException("maxDelay must be positive");
        }
    }

    public boolean isWithinTolerance(LocalDateTime observedAt) {
        return !isStale(observedAt);
    }

    public boolean isStale(LocalDateTime observedAt) {
        Objects.requireNonNull(observedAt, "observedAt must not be null");
        Duration age = Duration.between(toInstant(observedAt), clock.instant());
        return age.compareTo(maxDelay) > 0;
    }

    public Duration ageOf(LocalDateTime observedAt) {
        Objects.requireNonNull(observedAt, "observedAt must not be null");
        return Duration.between(toInstant(observedAt), clock.instant());
    }

    public ProjectionFreshness freshnessOf(LocalDateTime observedAt) {
        Duration age = ageOf(observedAt);
        return new ProjectionFreshness(age, maxDelay, age.compareTo(maxDelay) > 0);
    }

    private java.time.Instant toInstant(LocalDateTime timestamp) {
        ZoneId zone = clock.getZone();
        return timestamp.atZone(zone).toInstant();
    }
}
