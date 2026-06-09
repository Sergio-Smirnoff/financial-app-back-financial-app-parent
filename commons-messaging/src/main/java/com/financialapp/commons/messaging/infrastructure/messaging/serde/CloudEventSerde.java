package com.financialapp.commons.messaging.infrastructure.messaging.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financialapp.commons.messaging.domain.model.OutboxRecord;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class CloudEventSerde {

    private final ObjectMapper objectMapper;

    public CloudEventSerde(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CloudEvent toCloudEvent(OutboxRecord record) {
        return CloudEventBuilder.v1()
                .withId(record.eventId().value())
                .withSource(URI.create("/financial-app/" + record.source()))
                .withType(record.type().value())
                .withTime(OffsetDateTime.now(ZoneOffset.UTC))
                .withSubject(record.key())
                .withDataSchema(URI.create(record.dataSchema()))
                .withData("application/json", record.dataJson().getBytes(StandardCharsets.UTF_8))
                .build();
    }

    public <T> T dataAs(CloudEvent event, Class<T> type) {
        if (event.getData() == null) {
            throw new IllegalArgumentException("CloudEvent " + event.getId() + " has no data");
        }
        try {
            return objectMapper.readValue(event.getData().toBytes(), type);
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Failed to deserialize CloudEvent data for type " + type.getName(), ex);
        }
    }
}
