package com.financialapp.commons.messaging.infrastructure.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaCloudEventConfigTest {

    @Test
    void registersCloudEventInfrastructure() {
        new ApplicationContextRunner()
                .withBean(ObjectMapper.class, ObjectMapper::new)
                .withConfiguration(AutoConfigurations.of(KafkaCloudEventConfig.class))
                .run(ctx -> {
                    assertThat(ctx).hasNotFailed();
                    assertThat(ctx).hasSingleBean(KafkaTemplate.class);
                    assertThat(ctx).hasBean("cloudEventSerde");
                });
    }
}
