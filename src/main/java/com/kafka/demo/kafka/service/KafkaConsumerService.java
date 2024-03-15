package com.kafka.demo.kafka.service;

import java.time.Duration;
import java.util.Iterator;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kafka.demo.model.TaskStatus;

@Service
public class KafkaConsumerService {

    private final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    KafkaConsumer<String, TaskStatus> kafkaConsumer;

    public TaskStatus getLatestTaskStatus(String taskId) {

        ConsumerRecord<String, TaskStatus> latestUpdate = null;
        ConsumerRecords<String, TaskStatus> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(1000));

        if (!consumerRecords.isEmpty()) {
            Iterator itr = consumerRecords.records(taskId).iterator();

            while (itr.hasNext()) {
                latestUpdate = (ConsumerRecord<String, TaskStatus>) itr.next();
            }
            
            LOGGER.info("Latest updated status : " + latestUpdate.value());
        }
        return latestUpdate != null ? latestUpdate.value() : null;
    }
}
