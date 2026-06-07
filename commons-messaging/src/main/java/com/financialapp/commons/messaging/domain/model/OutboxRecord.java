package com.financialapp.commons.messaging.domain.model;

public record OutboxRecord(
        EventId eventId,
        String topic,
        String key,
        EventType type,
        String source,
        String dataSchema,
        String dataJson
) {
    public OutboxRecord {
        if (eventId == null) throw new IllegalArgumentException("eventId must not be null");
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
        return new OutboxRecord(EventId.newId(), topic, key, type,
                source, dataSchema, dataJson);
    }

    private static void require(String v, String name) {
        if (v == null || v.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
