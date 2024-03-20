package com.kafka.demo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class DemoApplication {

	static Faker faker = new Faker();
	private static final String INPUT_TOPIC = "input_topic";
	private static final String OUTPUT_TOPIC = "output_topic";

	@Value(value = "${spring.kafka.bootstrap-servers}")
	private static String bootstrapAddress;

	record VehicleIdType (String value) {}
	record Identifiers(String vehicleId, VehicleIdType vehicleIdType) {}
	record VehiclePayload(String updated, Boolean value) {}
	record Alerts(Map<VehicleActions, VehiclePayload> alertEvent){}
	record Current(Alerts alerts){}
	record Previous(Alerts alerts){}
	record Notification(
			String notificationType,
			String vehicleId,
			Identifiers identifiers,
			String created,
			Current current,
			Previous previous
	) { }

	// Define output records
	record Samples(Long timestampMs, String type, boolean value) {}
	record Data(String dataId, List<Samples> samples) {}
	record TransformedData(String uniqueId, String queryId, List<Data> data, Long eventTimestampMs) {}

	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner() {
		return  args -> {
			// Start producing Messages
			produceMessages();
		};
	}

	private static Properties setProps(String propType) {
		// Producer properties
		Properties props = new Properties();

			props.put("bootstrap.servers", "localhost:9092");
			props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			return props;
		
	}

	private static String generatePayload(Integer recordCount) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		String vehicleId = faker.idNumber().ssnValid();
		Notification[] notifications = new Notification[recordCount];

		// Get current LocalDateTime
		LocalDateTime currentLocalDateTime = LocalDateTime.now();

		// Create DateTimeFormatter instance with specified format
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

		// Format LocalDateTime to String
		String formattedDateTime = currentLocalDateTime.format(dateTimeFormatter);

		for (int i = 0; i < notifications.length; i++) {

			Map<VehicleActions, VehiclePayload> dynamicData = new HashMap<>();
			dynamicData.put(VehicleActions.DOOR_CLOSE, new VehiclePayload(formattedDateTime, true));

			Notification notification = new Notification(
					"STATUS_UPDATED",
					vehicleId,
					new Identifiers(
							vehicleId,
							new VehicleIdType("VIN")
					),
					formattedDateTime,
					new Current(
							new Alerts(
								dynamicData
							)
					),
					new Previous(
							new Alerts(
									dynamicData
							)
					)
			);
			notifications[i] = notification;
		}

		return objectMapper.writeValueAsString(notifications);
	}

	private static void produceMessages() {
		// Produce messages on a thread
		KafkaProducer<String, String> producer = new KafkaProducer<>(setProps("producer"));

		new Thread(() -> {
			while (true) {
                ProducerRecord<String, String> record = null;
                try {
					String message = generatePayload(1);
                    record = new ProducerRecord<>(INPUT_TOPIC, message);
					producer.send(record);
					System.out.println("Message published successfully to Topic: " + INPUT_TOPIC + ", Message: " + message);
					TimeUnit.SECONDS.sleep(2);
                } catch (JsonProcessingException | InterruptedException e) {
					e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
		}).start();
	}

	private static void processMessages() {

		StreamsBuilder builder = new StreamsBuilder();
		ObjectMapper objectMapper = new ObjectMapper();
		KStream<String, String> source = builder.stream(INPUT_TOPIC);

		new Thread(() -> {
			// Process the messages
			KStream<String, String> processedStream = source.mapValues(value -> {
				// Transform message
				try {
					List<Notification> notifications =Arrays.asList(objectMapper.readValue(value, Notification[].class));

					List<Samples> samples = Arrays.asList(new Samples(Instant.now().toEpochMilli(), "DATA_STRING",
							notifications.get(0).current.alerts().alertEvent().get(VehicleActions.DOOR_CLOSE).value));
					List<Data> dataRecord = Collections.singletonList(new Data("TSDP-CentralLockStatus", samples));

					TransformedData transformedData = new TransformedData(faker.idNumber().ssnValid(), faker.idNumber().ssnValid(), dataRecord, Instant.now().toEpochMilli());

					System.out.println("Writing to Topic: " + OUTPUT_TOPIC + ". Message: " + transformedData);
					return objectMapper.writeValueAsString(transformedData);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
					throw new RuntimeException("Failed to process JSON: ", e);
				}
			});

			processedStream.to(OUTPUT_TOPIC);

			KafkaStreams streams = new KafkaStreams(builder.build(), setProps("consumer"));
			streams.start();

			// Add shutdown hook to cleanly close the streams application
			Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
		}).start();
	}
}