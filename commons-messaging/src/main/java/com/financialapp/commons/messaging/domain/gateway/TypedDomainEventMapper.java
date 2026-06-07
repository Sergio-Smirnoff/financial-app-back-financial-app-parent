package com.financialapp.commons.messaging.domain.gateway;

import com.financialapp.commons.messaging.domain.model.OutboxRecord;
import java.util.List;

public abstract class TypedDomainEventMapper<E> implements DomainEventMapper {

    private final Class<E> eventType;

    protected TypedDomainEventMapper(Class<E> eventType) {
        this.eventType = eventType;
    }

    @Override
    public boolean supports(Object event) {
        return eventType.isInstance(event);
    }

    @Override
    public List<OutboxRecord> toOutboxRecords(Object event) {
        return mapTyped(eventType.cast(event));
    }

    protected abstract List<OutboxRecord> mapTyped(E event);
}
