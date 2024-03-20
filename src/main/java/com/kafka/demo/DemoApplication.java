package com.kafka.demo;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner() {

		// Kafka producer configuration
		Properties producerProps = new Properties();
		producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		// Kafka consumer configuration
		Properties consumerProps = new Properties();
		consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "my-consumer-group");
		consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

		// Create Kafka producer
		KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);

		// Create Kafka consumer
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);

		// Publish to Kafka producer every 5 seconds
		String topic = "my-topic";
		// Faker faker = new Faker();
		String message = """
							{
					"notificationType": "STATUS_UPDATED",
					"vehicleId": "SLATESTBOTWAUA001",
					"identifiers": {
						"vehicleId": "SLATESTBOTWAUA001",
						"vehicleIdType": {
							"value": "VIN"
						}
					},
					"created": "2023-08-23T12:42:09.814298Z",
					"current": {
						"alerts": {
							"alertEvent": {
								"vehicleUnlocked": {
									"updated": "2023-08-23T12:42:05Z",
									"value": "TRUE"
								}
							}
						}
					},
					"previous": {
						"alerts": {
							"alertEvent": {
								"vehicleUnlocked": {
									"updated": "2023-08-23T12:41:21Z",
									"value": "FALSE"
								}
							}
						}
					}
				}
								""";

		// Publish to Kafka producer every 5 seconds on a separate thread
		Thread producerThread = new Thread(() -> {
			while (true) {
				ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
				producer.send(new ProducerRecord<>(topic, message));
				System.out.println("Message published successfully. Topic: " + topic + ", Message: " + message);
				try {
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		producerThread.start();

		// Consume from Kafka consumer on a separate thread
		return args -> {
			consumer.subscribe(Collections.singletonList(topic));
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
				for (ConsumerRecord<String, String> consumerRecord : records) {
					System.out.println("Received message: " + consumerRecord.value());
				}
			}
		};
	}

}