package com.monisom.kafka;

import com.monisom.kafka.model.Alert;
import com.monisom.kafka.model.WindowedReading;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerde;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, WindowedReading> windowReadingConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfig("window-persister"),
                new StringDeserializer(),
                new JacksonJsonSerde<>(WindowedReading.class).noTypeInfo().deserializer());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, WindowedReading> windowReadingKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, WindowedReading> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(windowReadingConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Alert> alertConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfig("alert-persister"),
                new StringDeserializer(),
                new JacksonJsonSerde<>(Alert.class).noTypeInfo().deserializer());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Alert> alertKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Alert> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(alertConsumerFactory());
        return factory;
    }

    private Map<String, Object> consumerConfig(String groupId) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return config;
    }
}
