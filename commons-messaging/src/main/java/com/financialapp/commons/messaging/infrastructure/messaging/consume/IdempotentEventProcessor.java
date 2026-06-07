package com.financialapp.commons.messaging.infrastructure.messaging.consume;

import com.financialapp.commons.messaging.domain.gateway.ProcessedEventGateway;
import com.financialapp.commons.messaging.domain.model.EventId;
import com.financialapp.commons.messaging.infrastructure.messaging.serde.CloudEventSerde;
import io.cloudevents.CloudEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Slf4j
public class IdempotentEventProcessor {

    private final ProcessedEventGateway processedEvents;
    private final CloudEventSerde serde;

    public IdempotentEventProcessor(ProcessedEventGateway processedEvents, CloudEventSerde serde) {
        this.processedEvents = processedEvents;
        this.serde = serde;
    }

    @Transactional
    public <T> void process(CloudEvent event, Class<T> dataType, Consumer<T> handler) {
        EventId id = new EventId(event.getId());
        if (processedEvents.isProcessed(id)) {
            log.warn("Duplicate event {} (type={}) — skipping", id.value(), event.getType());
            return;
        }
        T data = serde.dataAs(event, dataType);
        handler.accept(data);
        processedEvents.markProcessed(id);
    }
}
