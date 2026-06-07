package com.financialapp.commons.messaging.infrastructure.messaging.relay;

import com.financialapp.commons.messaging.domain.gateway.OutboxGateway;
import com.financialapp.commons.messaging.domain.model.EventType;
import com.financialapp.commons.messaging.domain.model.OutboxRecord;
import com.financialapp.commons.messaging.infrastructure.messaging.serde.CloudEventSerde;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OutboxRelayTest {

    @Test
    void shipsUnsentRecordsAndMarksThemSent() {
        OutboxGateway gateway = mock(OutboxGateway.class);
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, CloudEvent> template = mock(KafkaTemplate.class);
        when(template.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));

        OutboxRecord r = OutboxRecord.create("users.user.registered", "42",
                new EventType("users.user.registered"), "ms-users", "schema://v1", "{}");
        when(gateway.findUnsent(anyInt())).thenReturn(List.of(r));

        OutboxRelay relay = new OutboxRelay(gateway, template,
                new CloudEventSerde(new ObjectMapper()), 100);
        relay.flush();

        verify(template).send(eq("users.user.registered"), eq("42"), any(CloudEvent.class));
        verify(gateway).markSent(r.eventId());
    }

    @Test
    void leavesRowUnsentWhenSendFails() {
        OutboxGateway gateway = mock(OutboxGateway.class);
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, CloudEvent> template = mock(KafkaTemplate.class);
        when(template.send(anyString(), anyString(), any()))
                .thenThrow(new RuntimeException("broker down"));

        OutboxRecord r = OutboxRecord.create("t", "1", new EventType("x"),
                "ms-x", "schema://v1", "{}");
        when(gateway.findUnsent(anyInt())).thenReturn(List.of(r));

        OutboxRelay relay = new OutboxRelay(gateway, template,
                new CloudEventSerde(new ObjectMapper()), 100);
        relay.flush();

        verify(gateway, never()).markSent(any());
    }
}
