package com.financialapp.commons.messaging.domain.gateway;

public interface ProcessedEventGateway {
    boolean isProcessed(String eventId);
    void markProcessed(String eventId);
}
