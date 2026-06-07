package com.financialapp.commons.messaging.domain.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OutboxRecordTest {

    @Test
    void buildsWithAGeneratedEventIdWhenNotSupplied() {
        OutboxRecord r = OutboxRecord.create(
                "users.user.registered", "42", new EventType("users.user.registered"),
                "ms-users", "https://schemas.financial-app/users/user-registered/v1",
                "{\"email\":\"a@b.c\"}");
        assertThat(r.eventId()).isNotBlank();
        assertThat(r.topic()).isEqualTo("users.user.registered");
        assertThat(r.key()).isEqualTo("42");
    }

    @Test
    void rejectsBlankTopic() {
        assertThatThrownBy(() -> OutboxRecord.create(
                " ", "42", new EventType("x"), "ms-users", "schema", "{}"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void eventTypeRejectsBlank() {
        assertThatThrownBy(() -> new EventType(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
