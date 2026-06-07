package com.financialapp.commons.messaging.infrastructure.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financialapp.commons.messaging.domain.gateway.DomainEventMapper;
import com.financialapp.commons.messaging.domain.gateway.OutboxGateway;
import com.financialapp.commons.messaging.domain.gateway.ProcessedEventGateway;
import com.financialapp.commons.messaging.infrastructure.messaging.consume.IdempotentEventProcessor;
import com.financialapp.commons.messaging.infrastructure.messaging.error.StandardDlqErrorHandler;
import com.financialapp.commons.messaging.infrastructure.messaging.relay.OutboxEventPublisher;
import com.financialapp.commons.messaging.infrastructure.messaging.relay.OutboxRelay;
import com.financialapp.commons.messaging.infrastructure.messaging.serde.CloudEventSerde;
import io.cloudevents.CloudEvent;
import io.cloudevents.kafka.CloudEventDeserializer;
import io.cloudevents.kafka.CloudEventSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoConfiguration
@EnableScheduling
public class KafkaCloudEventConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9093}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:${spring.application.name}-group}")
    private String groupId;

    @Bean
    public ProducerFactory<String, CloudEvent> cloudEventProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CloudEventSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, CloudEvent> kafkaTemplate(ProducerFactory<String, CloudEvent> pf) {
        return new KafkaTemplate<>(pf);
    }

    @Bean
    public ConsumerFactory<String, CloudEvent> cloudEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CloudEventDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CloudEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, CloudEvent> cf,
            KafkaTemplate<String, CloudEvent> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, CloudEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cf);
        DefaultErrorHandler handler = StandardDlqErrorHandler.create(kafkaTemplate);
        factory.setCommonErrorHandler(handler);
        return factory;
    }

    @Bean
    public CloudEventSerde cloudEventSerde(ObjectMapper objectMapper) {
        return new CloudEventSerde(objectMapper);
    }

    @Bean
    @ConditionalOnBean(OutboxGateway.class)
    public OutboxEventPublisher outboxEventPublisher(List<DomainEventMapper> mappers,
                                                     OutboxGateway outboxGateway) {
        return new OutboxEventPublisher(mappers, outboxGateway);
    }

    @Bean
    @ConditionalOnBean(OutboxGateway.class)
    public OutboxRelay outboxRelay(OutboxGateway outboxGateway,
                                   KafkaTemplate<String, CloudEvent> kafkaTemplate,
                                   CloudEventSerde serde,
                                   @Value("${messaging.outbox.batch-size:100}") int batchSize) {
        return new OutboxRelay(outboxGateway, kafkaTemplate, serde, batchSize);
    }

    @Bean
    @ConditionalOnBean(ProcessedEventGateway.class)
    public IdempotentEventProcessor idempotentEventProcessor(ProcessedEventGateway processedEvents,
                                                             CloudEventSerde serde) {
        return new IdempotentEventProcessor(processedEvents, serde);
    }
}
