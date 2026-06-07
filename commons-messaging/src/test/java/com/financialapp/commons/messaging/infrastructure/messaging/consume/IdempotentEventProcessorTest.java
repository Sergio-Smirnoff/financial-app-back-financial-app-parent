package com.financialapp.commons.messaging.infrastructure.messaging.consume;

import com.financialapp.commons.messaging.domain.gateway.ProcessedEventGateway;
import com.financialapp.commons.messaging.domain.model.EventId;
import com.financialapp.commons.messaging.infrastructure.messaging.serde.CloudEventSerde;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class IdempotentEventProcessorTest {

    record Sample(String email) {}

    private CloudEvent sample(String id) {
        return CloudEventBuilder.v1()
                .withId(id).withSource(URI.create("/x")).withType("t")
                .withData("application/json", "{\"email\":\"a@b.c\"}".getBytes(StandardCharsets.UTF_8))
                .build();
    }

    @Test
    void handlesNewEventAndRecordsIt() {
        ProcessedEventGateway gateway = mock(ProcessedEventGateway.class);
        when(gateway.isProcessed(new EventId("e1"))).thenReturn(false);
        var processor = new IdempotentEventProcessor(gateway, new CloudEventSerde(new ObjectMapper()));
        AtomicReference<String> seen = new AtomicReference<>();

        processor.process(sample("e1"), Sample.class, s -> seen.set(s.email()));

        assertThat(seen.get()).isEqualTo("a@b.c");
        verify(gateway).markProcessed(new EventId("e1"));
    }

    @Test
    void skipsDuplicate() {
        ProcessedEventGateway gateway = mock(ProcessedEventGateway.class);
        when(gateway.isProcessed(new EventId("e1"))).thenReturn(true);
        var processor = new IdempotentEventProcessor(gateway, new CloudEventSerde(new ObjectMapper()));
        AtomicReference<String> seen = new AtomicReference<>();

        processor.process(sample("e1"), Sample.class, s -> seen.set(s.email()));

        assertThat(seen.get()).isNull();
        verify(gateway, never()).markProcessed(any());
    }
}
