package com.kafka.demo.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.kafka.demo.model.MessageObject;

@Configuration
public class KafkaConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${spring.kafka.reset-config}")
    private String autoOffsetResetConfig;

    @Value(value = "${spring.kafka.group-id-config}")
    private String groupIdConfig;

    @Value(value = "${spring.kafka.trusted-packages}")
    private String trustedPackages;

    @Autowired
    KafkaProperties kafkaProperties;

    @Bean
    KafkaAdmin kafkaAdmin() {
        KafkaAdmin kafkaAdmin = new KafkaAdmin(kafkaProperties.buildAdminProperties());
        kafkaAdmin.setFatalIfBrokerNotAvailable(kafkaProperties.getAdmin().isFailFast());
        return kafkaAdmin;
    }

    @Bean
    ConsumerFactory<String, MessageObject> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackages);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupIdConfig);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetResetConfig);

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    KafkaConsumer<String, MessageObject> kafkaConsumer() {
        return (KafkaConsumer<String, MessageObject>) consumerFactory().createConsumer();
    }

    @Bean
    ProducerFactory<String, MessageObject> producerFactory() {

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    KafkaTemplate<String, MessageObject> kafkaTemplate() {
        var kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setConsumerFactory(consumerFactory());
        return kafkaTemplate;
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, MessageObject> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MessageObject> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
