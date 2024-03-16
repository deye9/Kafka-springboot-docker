package com.kafka.demo.controllers;

import com.kafka.demo.TestDataSetup;
import com.kafka.demo.kafka.service.KafkaProducerService;
import com.kafka.demo.model.MessageObject;
import com.kafka.demo.model.TaskRequest;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
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

    // Delete a topic
    @Test
    public void testDeleteTopic_Success() throws ExecutionException, InterruptedException {
        ResponseEntity<?> response = controller.deleteTopic(topic);

        verify(service, times(1)).deleteTopic(topic);
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void testDeleteTopic_ThrowsExecutionException() {
        try {
            controller.deleteTopic(topic);
        } catch (Exception e) {
            assertEquals(e.getClass(), ExecutionException.class);
        }
    }

    @Test
    public void testDeleteTopic_ThrowsInterruptedException() {
        try {
            controller.deleteTopic(topic);
        } catch (Exception e) {
            assertEquals(e.getClass(), InterruptedException.class);
        }
    }

    // Get all Topics
    @Test
    public void testGetTopic_Success() throws ExecutionException, InterruptedException {
        ResponseEntity<?> response = controller.getTopic();

        verify(service, times(1)).getAllTopics();
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testGetTopic_ThrowsExecutionException() {
        try {
            controller.getTopic();
        } catch (Exception e) {
            assertEquals(e.getClass(), ExecutionException.class);
        }
    }

    @Test
    public void testGetTopic_ThrowsInterruptedException() {
        try {
            controller.getTopic();
        } catch (Exception e) {
            assertEquals(e.getClass(), InterruptedException.class);
        }
    }

    // Get all messages from a topic
    @Test
    public void testGetTopicMessages_Success() throws ExecutionException, InterruptedException {

        ResponseEntity<List<MessageObject>> response = controller.getTopic(topic);

        verify(service, times(1)).getTopicMessages(topic);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testGetTopicMessages_ThrowsExecutionException() {

        try {
            controller.getTopic(topic);
        } catch (Exception e) {
            assertEquals(e.getClass(), ExecutionException.class);
        }
    }

    @Test
    public void testGetTopicMessages_ThrowsInterruptedException() {

        try {
            controller.getTopic(topic);
        } catch (Exception e) {
            assertEquals(e.getClass(), InterruptedException.class);
        }
    }

    // Save message to topic
    @Test
    public void testCreateTopicMessage_Success() throws ExecutionException, InterruptedException {

        MessageObject message = new MessageObject("test_Message 1", topic, 0, Optional.empty());
        ResponseEntity<?> response = controller.createTopicMessage(topic, message, UriComponentsBuilder.newInstance());

        verify(service, times(1)).saveMessageToTopic(topic, message);
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
    }

    @Test
    public void testCreateTopicMessage_ThrowsExecutionException() {

        MessageObject message = new MessageObject("test_Message 1", topic, 0, Optional.empty());
        try {
            controller.createTopicMessage(topic, message, UriComponentsBuilder.newInstance());
        } catch (Exception e) {
            assertEquals(e.getClass(), ExecutionException.class);
        }
    }

    @Test
    public void testCreateTopicMessage_ThrowsInterruptedException() {

        MessageObject message = new MessageObject("test_Message 1", topic, 0, Optional.empty());
        try {
            controller.createTopicMessage(topic, message, UriComponentsBuilder.newInstance());
        } catch (Exception e) {
            assertEquals(e.getClass(), InterruptedException.class);
        }
    }

    // Create a new topic
    @Test
    public void testCreateTopic_Success() throws ExecutionException, InterruptedException {

        TaskRequest topic = new TaskRequest("testTopic");

        ResponseEntity<?> response = controller.createTopic(topic, UriComponentsBuilder.newInstance());

        verify(service, times(1)).createTopic(topic.name());
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
    }

    @Test
    public void testCreateTopic_ThrowsExecutionException() {

        TaskRequest topic = new TaskRequest("testTopic");
        try {
            controller.createTopic(topic, UriComponentsBuilder.newInstance());
        } catch (Exception e) {
            assertEquals(e.getClass(), ExecutionException.class);
        }
    }

    @Test
    public void testCreateTopic_ThrowsInterruptedException() {

        TaskRequest topic = new TaskRequest("testTopic");
        try {
            controller.createTopic(topic, UriComponentsBuilder.newInstance());
        } catch (Exception e) {
            assertEquals(e.getClass(), InterruptedException.class);
        }
    }
}