package com.financialapp.commons.messaging.domain.model;

public record CeAttributes(
        String id,
        String source,
        String type,
        String subject,
        String dataSchema
) {
}
