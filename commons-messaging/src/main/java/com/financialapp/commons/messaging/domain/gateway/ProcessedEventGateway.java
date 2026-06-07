package com.financialapp.commons.messaging.domain.gateway;

import com.financialapp.commons.messaging.domain.model.EventId;

public interface ProcessedEventGateway {
    boolean isProcessed(EventId eventId);
    void markProcessed(EventId eventId);
}
