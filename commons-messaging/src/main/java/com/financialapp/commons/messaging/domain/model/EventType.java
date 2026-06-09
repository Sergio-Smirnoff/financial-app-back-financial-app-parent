package com.financialapp.commons.messaging.domain.model;

public record EventType(String value) {
    public EventType {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("event type must not be blank");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
