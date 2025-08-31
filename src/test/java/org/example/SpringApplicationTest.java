package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.SpringMain.*;

@Import(DatabaseLoader.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringApplicationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private AnswerController patientController;

	@Autowired
	private DatabaseLoader databaseLoader;

	@Test
	void healthCheckOk() throws Exception {
		ResponseEntity<String> forObject = restTemplate
				.getForEntity("http://localhost:" + port + "/health", String.class);
		assertThat(forObject.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
		assertThat(forObject.getBody())
				.contains("ok");
	}

	@Test
	void patientNotFound() throws Exception {
		ResponseEntity<String> forObject = restTemplate
				.getForEntity("http://localhost:" + port + "/patient/{id}", String.class, UUID.randomUUID());
		assertThat(forObject.getStatusCode().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
		assertThat(forObject.getBody())
				.contains("404 Could not find patient");
	}

	@Test
	void patientFound() throws Exception {
		UUID patientId = databaseLoader.testAnswers.getFirst().getId();
		ResponseEntity<String> forObject = restTemplate
				.getForEntity("http://localhost:" + port + "/patient/{id}", String.class, patientId);
		assertThat(forObject.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx");
		ZonedDateTime patientDob = databaseLoader.testAnswers.getFirst().getCreated().toInstant().atZone(ZoneId.systemDefault());
		assertThat(forObject.getBody())
				.isEqualTo(String.format(
						"{\"id\":\"%s\",\"firstName\":\"A\",\"lastName\":\"L\",\"dob\":\"%s\"," +
						"\"ssn\":\"1234\",\"phoneNumber\":null}",
						databaseLoader.testAnswers.getFirst().getId().toString(),
						dateTimeFormatter.format(patientDob)));
	}
}
