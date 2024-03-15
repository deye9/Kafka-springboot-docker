package com.kafka.demo.kafka.service;

import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.kafka.demo.model.TaskStatus;

@Service
public class KafkaProducerService {

    @Autowired
    KafkaTemplate<String, TaskStatus> kafkaTemplate;

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    private final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);

    private boolean topicExists(String topicName) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);

        try (AdminClient adminClient = AdminClient.create(props)) {
            return adminClient.listTopics().names().get().contains(topicName);
        } catch (Exception e) {
            LOGGER.error("Error while checking if topic exists: " + e.getMessage());
            return false;
        }
    }

    private CreateTopicsResult createTopic(String topicName) {

        LOGGER.info("Creating topic: " + topicName);

        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);

        try (AdminClient adminClient = AdminClient.create(props)) {
            return adminClient.createTopics(Collections.singleton(new NewTopic(topicName, 1, (short) 1)));
        } catch (Exception e) {
            LOGGER.error("Error while creating topic: " + e.getMessage());
            return null;
        }
    }

    public void send(String topicName, String key, TaskStatus value) {

        if (!topicExists(topicName)) {
            LOGGER.info("Topic does not exist: " + topicName);

            CreateTopicsResult result = createTopic(topicName);
            result.values().forEach((name, future) -> {
                try {
                    future.get();
                    LOGGER.info("Topic created: " + name);
                } catch (Exception e) {
                    LOGGER.error("Error while creating topic: " + e.getMessage() + ". Aborting operation.");
                    return;
                }
            });
        }

        var future = kafkaTemplate.send(topicName, key, value);

        future.whenComplete((sendResult, exception) -> {
            if (exception != null) {
                future.completeExceptionally(exception);
            } else {
                future.complete(sendResult);
            }
            LOGGER.info("Task status send to Kafka topic : " + value);
        });
    }
}