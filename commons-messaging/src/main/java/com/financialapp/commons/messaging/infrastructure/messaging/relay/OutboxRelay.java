package com.financialapp.commons.messaging.infrastructure.messaging.relay;

import com.financialapp.commons.messaging.domain.gateway.OutboxGateway;
import com.financialapp.commons.messaging.domain.model.OutboxRecord;
import com.financialapp.commons.messaging.infrastructure.messaging.serde.CloudEventSerde;
import io.cloudevents.CloudEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
public class OutboxRelay {

    private final OutboxGateway outboxGateway;
    private final KafkaTemplate<String, CloudEvent> kafkaTemplate;
    private final CloudEventSerde serde;
    private final int batchSize;

    public OutboxRelay(OutboxGateway outboxGateway,
                       KafkaTemplate<String, CloudEvent> kafkaTemplate,
                       CloudEventSerde serde,
                       int batchSize) {
        this.outboxGateway = outboxGateway;
        this.kafkaTemplate = kafkaTemplate;
        this.serde = serde;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${messaging.outbox.poll-ms:2000}")
    @Transactional
    public void flush() {
        List<OutboxRecord> batch = outboxGateway.findUnsent(batchSize);
        for (OutboxRecord record : batch) {
            try {
                CloudEvent event = serde.toCloudEvent(record);
                kafkaTemplate.send(record.topic(), record.key(), event).join();
                outboxGateway.markSent(record.eventId());
            } catch (Exception ex) {
                log.error("Outbox relay failed for event {}: {}", record.eventId().value(), ex.getMessage());
            }
        }
    }
}
