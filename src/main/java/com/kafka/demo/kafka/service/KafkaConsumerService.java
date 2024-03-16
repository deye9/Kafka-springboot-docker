package com.kafka.demo.kafka.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kafka.demo.model.MessageObject;

@Service
public class KafkaConsumerService {

    @Autowired
    KafkaConsumer<String, MessageObject> consumer;

    public List<MessageObject> getTopicMessages(String topic) {

        consumer.subscribe(Arrays.asList(topic));
        List<MessageObject> messageList = new ArrayList<>();

        while (true) {
            ConsumerRecords<String, MessageObject> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, MessageObject> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(),
                        record.key(), record.value());

                if (!records.isEmpty()) {
                    Iterator<ConsumerRecord<String, MessageObject>> itr = records.iterator();
                    while (itr.hasNext()) {
                        messageList.add(itr.next().value());
                    }
                }
            }
            break; // Add break statement to exit the loop
        }

        return messageList;
    }

}
