package com.financialapp.commons.messaging.infrastructure.messaging.relay;

import com.financialapp.commons.messaging.domain.gateway.DomainEventMapper;
import com.financialapp.commons.messaging.domain.gateway.OutboxGateway;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class OutboxEventPublisher {

    private final List<DomainEventMapper> mappers;
    private final OutboxGateway outboxGateway;

    public OutboxEventPublisher(List<DomainEventMapper> mappers, OutboxGateway outboxGateway) {
        this.mappers = mappers;
        this.outboxGateway = outboxGateway;
    }

    public void publish(Object event) {
        for (DomainEventMapper mapper : mappers) {
            if (mapper.supports(event)) {
                mapper.toOutboxRecords(event).forEach(outboxGateway::save);
                return;
            }
        }
        log.warn("No DomainEventMapper found for event type {}; event dropped",
                event.getClass().getName());
    }
}
