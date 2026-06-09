package com.financialapp.commons.messaging.infrastructure.messaging.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financialapp.commons.messaging.domain.model.EventType;
import com.financialapp.commons.messaging.domain.model.OutboxRecord;
import io.cloudevents.CloudEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CloudEventSerdeTest {

    record Sample(String email) {}

    private final CloudEventSerde serde = new CloudEventSerde(new ObjectMapper());

    @Test
    void buildsBinaryCloudEventFromOutboxRecord() {
        OutboxRecord r = OutboxRecord.create(
                "users.user.registered", "42", new EventType("users.user.registered"),
                "ms-users", "https://schemas.financial-app/users/user-registered/v1",
                "{\"email\":\"a@b.c\"}");

        CloudEvent ce = serde.toCloudEvent(r);

        assertThat(ce.getId()).isEqualTo(r.eventId().value());
        assertThat(ce.getType()).isEqualTo("users.user.registered");
        assertThat(ce.getSubject()).isEqualTo("42");
        assertThat(ce.getSource().toString()).isEqualTo("/financial-app/ms-users");
        assertThat(ce.getDataSchema().toString())
                .isEqualTo("https://schemas.financial-app/users/user-registered/v1");
        assertThat(ce.getData()).isNotNull();
    }

    @Test
    void deserializesDataToTargetType() {
        OutboxRecord r = OutboxRecord.create(
                "users.user.registered", "42", new EventType("users.user.registered"),
                "ms-users", "schema://v1", "{\"email\":\"a@b.c\"}");
        CloudEvent ce = serde.toCloudEvent(r);

        Sample s = serde.dataAs(ce, Sample.class);

        assertThat(s.email()).isEqualTo("a@b.c");
    }
}
