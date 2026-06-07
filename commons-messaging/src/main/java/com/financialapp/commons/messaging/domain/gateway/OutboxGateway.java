package com.financialapp.commons.messaging.domain.gateway;

import com.financialapp.commons.messaging.domain.model.OutboxRecord;
import java.util.List;

public interface OutboxGateway {
    void save(OutboxRecord record);
    List<OutboxRecord> findUnsent(int batchSize);
    void markSent(String eventId);
}
