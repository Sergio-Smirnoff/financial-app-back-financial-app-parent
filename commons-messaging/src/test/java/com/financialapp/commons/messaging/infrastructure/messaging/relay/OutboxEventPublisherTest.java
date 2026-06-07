package com.financialapp.commons.messaging.infrastructure.messaging.relay;

import com.financialapp.commons.messaging.domain.gateway.DomainEventMapper;
import com.financialapp.commons.messaging.domain.gateway.OutboxGateway;
import com.financialapp.commons.messaging.domain.model.EventType;
import com.financialapp.commons.messaging.domain.model.OutboxRecord;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class OutboxEventPublisherTest {

    record Evt(String userId) {}

    @Test
    void mapsAndSavesEachRecordViaTheMatchingMapper() {
        OutboxGateway gateway = mock(OutboxGateway.class);
        OutboxRecord rec = OutboxRecord.create("t", "1", new EventType("x"), "ms-x", "s", "{}");
        DomainEventMapper mapper = new DomainEventMapper() {
            public boolean supports(Object event) { return event instanceof Evt; }
            public List<OutboxRecord> toOutboxRecords(Object event) { return List.of(rec); }
        };

        OutboxEventPublisher publisher = new OutboxEventPublisher(List.of(mapper), gateway);
        publisher.publish(new Evt("1"));

        verify(gateway).save(rec);
    }

    @Test
    void ignoresEventsNoMapperSupports() {
        OutboxGateway gateway = mock(OutboxGateway.class);
        OutboxEventPublisher publisher = new OutboxEventPublisher(List.of(), gateway);
        publisher.publish(new Evt("1"));
        verifyNoInteractions(gateway);
    }
}
