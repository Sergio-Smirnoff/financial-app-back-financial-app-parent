package com.financialapp.commons.messaging.domain.model;

import java.util.UUID;

public record EventId(String value) {
    public EventId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("event id must not be blank");
        }
    }

    public static EventId newId() {
        return new EventId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
