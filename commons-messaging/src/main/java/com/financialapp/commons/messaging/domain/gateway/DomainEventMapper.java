package com.financialapp.commons.messaging.domain.gateway;

import com.financialapp.commons.messaging.domain.model.OutboxRecord;
import java.util.List;

public interface DomainEventMapper {
    boolean supports(Object event);
    List<OutboxRecord> toOutboxRecords(Object event);
}
