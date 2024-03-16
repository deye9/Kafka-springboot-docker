package com.kafka.demo.kafka.service;

import java.time.Duration;
import java.util.*;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Service;

import com.kafka.demo.model.MessageObject;

@Service
public class KafkaConsumerService {

    final
    KafkaConsumer<String, MessageObject> consumer;

    public KafkaConsumerService(KafkaConsumer<String, MessageObject> consumer) {
        this.consumer = consumer;
    }

    public List<MessageObject> getTopicMessages(String topic) {

        consumer.subscribe(Collections.singletonList(topic));
        List<MessageObject> messageList = new ArrayList<>();

        do {
            ConsumerRecords<String, MessageObject> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, MessageObject> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(),
                        record.key(), record.value());

                if (!records.isEmpty()) {
                    for (ConsumerRecord<String, MessageObject> stringMessageObjectConsumerRecord : records) {
                        messageList.add(stringMessageObjectConsumerRecord.value());
                    }
                }
            }
            break; // Add break statement to exit the loop
        } while (true);

        return messageList;
    }

}
