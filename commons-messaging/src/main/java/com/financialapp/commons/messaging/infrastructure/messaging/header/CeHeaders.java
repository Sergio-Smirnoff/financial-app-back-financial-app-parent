package com.financialapp.commons.messaging.infrastructure.messaging.header;

public final class CeHeaders {
    public static final String ID = "ce_id";
    public static final String SOURCE = "ce_source";
    public static final String TYPE = "ce_type";
    public static final String SPEC_VERSION = "ce_specversion";
    public static final String TIME = "ce_time";
    public static final String SUBJECT = "ce_subject";
    public static final String DATA_SCHEMA = "ce_dataschema";
    public static final String TRACE_PARENT = "traceparent";

    private CeHeaders() {
    }
}
