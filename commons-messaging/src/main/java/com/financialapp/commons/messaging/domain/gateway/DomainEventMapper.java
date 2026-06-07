package com.financialapp.commons.messaging.domain.gateway;

import com.financialapp.commons.messaging.domain.model.OutboxRecord;
import java.util.List;

public interface DomainEventMapper<E> {
    boolean supports(Object event);
    List<OutboxRecord> toOutboxRecords(E event);
}
