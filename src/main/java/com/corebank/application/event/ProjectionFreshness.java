package com.corebank.application.event;

import java.time.Duration;
import java.util.Objects;

public record ProjectionFreshness(Duration age, Duration maxDelay, boolean stale) {

    public ProjectionFreshness {
        Objects.requireNonNull(age, "age must not be null");
        Objects.requireNonNull(maxDelay, "maxDelay must not be null");
    }

    public boolean withinTolerance() {
        return !stale;
    }
}