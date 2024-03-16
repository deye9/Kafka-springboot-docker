package com.kafka.demo;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(TestDataSetup.class)
class DemoApplicationTests {

	@Test
	void contextLoads() {
		assertNotNull(DemoApplication.class);
	}

}
