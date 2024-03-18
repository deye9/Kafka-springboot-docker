package com.kafka.demo.controllers;

import com.kafka.demo.TestDataSetup;
import com.kafka.demo.kafka.service.KafkaProducerService;
import com.kafka.demo.model.MessageObject;
import com.kafka.demo.model.TaskRequest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(TestDataSetup.class)
public class TopicControllerTest {

    String topic;

    @Mock
    KafkaProducerService service;

    @InjectMocks
    TopicController controller;

    @BeforeEach
    void setUp() {
        topic = "testTopic";
        MockitoAnnotations.openMocks(this);
    }

    // Get all Topics
    @Test
    void testGetTopic_Success() throws ExecutionException, InterruptedException {

        List<String> expectedTopic = List.of();
        when(service.getAllTopics()).thenReturn(expectedTopic);

        ResponseEntity<?> actualTopic = controller.getTopic();

        assertEquals(actualTopic.getStatusCode(), HttpStatus.OK);
        assertEquals(actualTopic.getBody(), expectedTopic);
    }

    @Test
    void testGetTopic_ThrowsExecutionException() throws ExecutionException, InterruptedException {

        when(service.getAllTopics()).thenThrow(ExecutionException.class);

        try {
            controller.getTopic();
        } catch (Exception e) {
            assertEquals(e.getClass(), ExecutionException.class);
        }
    }

    @Test
    void testGetTopic_ThrowsInterruptedException() throws ExecutionException, InterruptedException {

        when(service.getAllTopics()).thenThrow(InterruptedException.class);

        try {
            controller.getTopic();
        } catch (Exception e) {
            assertEquals(e.getClass(), InterruptedException.class);
        }
    }

    // Get all messages from a topic
    @Test
    void testGetTopicMessages_Success() throws ExecutionException, InterruptedException {

        List<MessageObject> expectedMessages = List.of();
        when(service.getTopicMessages(topic)).thenReturn(expectedMessages);

        ResponseEntity<?> actualMessages = controller.getTopicMessage(topic);

        assertEquals(actualMessages.getStatusCode(), HttpStatus.OK);
        assertEquals(actualMessages.getBody(), expectedMessages);
    }

    @Test
    void testGetTopicMessages_ThrowsExecutionException() throws ExecutionException, InterruptedException {

        when(service.getTopicMessages(topic)).thenThrow(ExecutionException.class);

        try {
            controller.getTopicMessage(topic);
        } catch (Exception e) {
            assertEquals(e.getClass(), ExecutionException.class);
        }
    }
    
    @Test
    void testGetTopicMessages_ThrowsInterruptedException() throws ExecutionException, InterruptedException {

        when(service.getTopicMessages(topic)).thenThrow(InterruptedException.class);

        try {
            controller.getTopicMessage(topic);
        } catch (Exception e) {
            assertEquals(e.getClass(), InterruptedException.class);
        }
    }

    // Create Topic
    @Test
    void testCreateTopic_Success() throws ExecutionException, InterruptedException {

        TaskRequest topic = new TaskRequest("testTopic");
        Map<String, String> expectedMessage = Collections.singletonMap("data", topic.name());

        ResponseEntity<?> response = controller.createTopic(topic, UriComponentsBuilder.newInstance());

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(response.getBody(), expectedMessage);
    }

    @Test
    void testCreateTopic_ThrowsExecutionException() {

        TaskRequest topic = new TaskRequest("testTopic");
        try {
            controller.createTopic(topic, UriComponentsBuilder.newInstance());
        } catch (Exception e) {
            assertEquals(e.getClass(), ExecutionException.class);
        }
    }

    @Test
    void testCreateTopic_ThrowsInterruptedException() {

        TaskRequest topic = new TaskRequest("testTopic");
        try {
            controller.createTopic(topic, UriComponentsBuilder.newInstance());
        } catch (Exception e) {
            assertEquals(e.getClass(), InterruptedException.class);
        }
    }

    // // Save message to topic
    // @Test
    // void testCreateTopicMessage_Success() throws ExecutionException, InterruptedException {

    //     MessageObject message = new MessageObject("id 1", topic, 0);

    //     ResponseEntity<?> response = controller.createTopicMessage(topic, message, UriComponentsBuilder.newInstance());

    //     Assertions.assertNull(response.getBody());
    //     assertEquals(response.getStatusCode(), HttpStatus.CREATED);
    // }

    // @Test
    // void testCreateTopicMessage_ThrowsExecutionException() {

    //     MessageObject message = new MessageObject("id 1", topic, 0);
    //     try {
    //         controller.createTopicMessage(topic, message, UriComponentsBuilder.newInstance());
    //     } catch (Exception e) {
    //         assertEquals(e.getClass(), ExecutionException.class);
    //     }
    // }

    // @Test
    // void testCreateTopicMessage_ThrowsInterruptedException() {

    //     MessageObject message = new MessageObject("id 1", topic, 0);
    //     try {
    //         controller.createTopicMessage(topic, message, UriComponentsBuilder.newInstance());
    //     } catch (Exception e) {
    //         assertEquals(e.getClass(), InterruptedException.class);
    //     }
    // }
   
    // Delete a topic
    @Test
    void testDeleteTopic_Success() throws ExecutionException, InterruptedException {

        ResponseEntity<?> response = controller.deleteTopic(topic);

        Assertions.assertNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }
}
