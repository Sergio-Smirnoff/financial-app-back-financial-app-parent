package com.financialapp.commons.messaging.domain.model;

import java.util.UUID;

public record OutboxRecord(
        String eventId,
        String topic,
        String key,
        EventType type,
        String source,
        String dataSchema,
        String dataJson
) {
    public OutboxRecord {
        require(eventId, "eventId");
        require(topic, "topic");
        require(key, "key");
        require(source, "source");
        require(dataSchema, "dataSchema");
        require(dataJson, "dataJson");
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
    }

    public static OutboxRecord create(String topic, String key, EventType type,
                                      String source, String dataSchema, String dataJson) {
        return new OutboxRecord(UUID.randomUUID().toString(), topic, key, type,
                source, dataSchema, dataJson);
    }

    private static void require(String v, String name) {
        if (v == null || v.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
