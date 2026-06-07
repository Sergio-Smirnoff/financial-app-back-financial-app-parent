package com.financialapp.commons.messaging.infrastructure.messaging.header;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CeHeadersTest {
    @Test
    void exposesBinaryModeHeaderNames() {
        assertThat(CeHeaders.ID).isEqualTo("ce_id");
        assertThat(CeHeaders.SOURCE).isEqualTo("ce_source");
        assertThat(CeHeaders.TYPE).isEqualTo("ce_type");
        assertThat(CeHeaders.SPEC_VERSION).isEqualTo("ce_specversion");
        assertThat(CeHeaders.TIME).isEqualTo("ce_time");
        assertThat(CeHeaders.SUBJECT).isEqualTo("ce_subject");
        assertThat(CeHeaders.DATA_SCHEMA).isEqualTo("ce_dataschema");
        assertThat(CeHeaders.TRACE_PARENT).isEqualTo("traceparent");
    }
}
