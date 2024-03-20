package com.kafka.demo.controllers;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.kafka.demo.kafka.service.KafkaProducerService;
import com.kafka.demo.model.TaskRequest;
import com.kafka.demo.model.MessageObject;

import jakarta.validation.Valid;

@RestController
public class TopicController {

    final KafkaProducerService service;

    public TopicController(KafkaProducerService service) {
        this.service = service;
    }

    // Delete a topic
    @DeleteMapping("/{topic}")
    public ResponseEntity<?> deleteTopic(@Valid @PathVariable("topic") String topic)
            throws ExecutionException, InterruptedException {

        service.deleteTopic(topic);

        return ResponseEntity.noContent().build();
    }

    // Get all topics
    @GetMapping("/topic")
    public ResponseEntity<?> getTopic() throws ExecutionException, InterruptedException {

        return ResponseEntity.ok().body(service.getAllTopics());
    }

    // Get all messages from a topic
    @GetMapping("/{topic}")
    public ResponseEntity<?> getTopicMessage(@Valid @PathVariable("topic") String topic)
            throws ExecutionException, InterruptedException {

        return ResponseEntity.ok().body(service.getTopicMessages(topic));
    }

    // Save message to topic
    @PostMapping("/{topic}/message")
    public ResponseEntity<?> createTopicMessage(@Valid @PathVariable("topic") String topic,
            @Valid @RequestBody List<MessageObject> message, UriComponentsBuilder b)
            throws InterruptedException {

        service.saveMessageToTopic(topic, message);
        UriComponents topicURL = b.path("/{topic}").buildAndExpand(topic);

        // Fire up the consumer service and let the topic be consumed right away

        return ResponseEntity.created(topicURL.toUri()).build();
    }

    // Create a new topic
    @PostMapping("/topic")
    public ResponseEntity<?> createTopic(@Valid @RequestBody TaskRequest taskRequest, UriComponentsBuilder b)
            throws ExecutionException, InterruptedException {

        service.createTopic(taskRequest.name());
        UriComponents topicURL = b.path("/topic/{topicName}").buildAndExpand(taskRequest.name());

        return ResponseEntity.created(topicURL.toUri()).body(Collections.singletonMap("data", taskRequest.name()));
    }

}
