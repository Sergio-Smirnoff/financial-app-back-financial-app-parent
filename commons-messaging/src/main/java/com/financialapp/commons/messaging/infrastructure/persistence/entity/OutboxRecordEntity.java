package com.financialapp.commons.messaging.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class OutboxRecordEntity {

    @Column(name = "event_id", nullable = false, unique = true, length = 64)
    private String eventId;

    @Column(nullable = false, length = 249)
    private String topic;

    @Column(name = "aggregate_key", nullable = false, length = 64)
    private String aggregateKey;

    @Column(name = "ce_type", nullable = false, length = 120)
    private String ceType;

    @Column(name = "ce_source", nullable = false, length = 255)
    private String ceSource;

    @Column(name = "data_schema", nullable = false, length = 512)
    private String dataSchema;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data_json", nullable = false, columnDefinition = "jsonb")
    private String dataJson;

    @Column(nullable = false)
    private boolean sent = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}
