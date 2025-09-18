package org.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.example.EchoRepository.Echo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Import(DatabaseLoader.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringApplicationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private DatabaseLoader databaseLoader;

	@Test
	void healthCheckOk() throws Exception {
		ResponseEntity<String> forObject = restTemplate
				.getForEntity("http://localhost:" + port + "/", String.class);
		assertThat(forObject.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
		assertThat(forObject.getBody())
				.contains("ok");
	}

	@Test
	void echoNotFound() throws Exception {
		UUID echoUUID = UUID.randomUUID();
		ResponseEntity<String> forObject = restTemplate
				.getForEntity("http://localhost:" + port + "/echos/{id}", String.class, echoUUID);
		assertThat(forObject.getStatusCode().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
		assertThat(forObject.getBody())
				.contains("404 Could not find "+echoUUID);
	}

	@Test
	void echoFound() throws Exception {
		UUID echoId = databaseLoader.testEchos.getFirst().getId();
		ResponseEntity<String> forString = restTemplate
			.getForEntity("http://localhost:" + port + "/echos/{id}", String.class, echoId);
		assertThat(forString.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxx");
		ZonedDateTime created = databaseLoader.testEchos.getFirst().getCreated().toInstant().atZone(ZoneOffset.UTC);
		ZonedDateTime modified = databaseLoader.testEchos.getFirst().getModified().toInstant().atZone(ZoneOffset.UTC);
		assertThat(forString.getBody())
			.isEqualTo(String.format(
				"{"+
					"\"id\":\"%s\",\"answer\":\"answer 1\","+
					"\"created\":\"%s\",\"modified\":\"%s\","+
					"\"_links\":{"+
						"\"self\":{\"href\":\"http://localhost:%s/echos/%s\"},"+
						"\"echos\":{\"href\":\"http://localhost:%s/echos\"}"+
					"}"+
				"}",
				echoId,
				dateTimeFormatter.format(created), dateTimeFormatter.format(modified),
				port, echoId, port
		));
		// Check object mapping
		ResponseEntity<Echo> forObject = restTemplate
			.getForEntity("http://localhost:" + port + "/echos/{id}", Echo.class, echoId);
		assertThat(forObject.getBody().id).isEqualTo(echoId);
		assertThat(forObject.getBody().answer).isEqualTo("answer 1");
		assertThat(forObject.getBody().created).isEqualTo(databaseLoader.testEchos.getFirst().getCreated());
		assertThat(forObject.getBody().modified).isEqualTo(forObject.getBody().created);
	}
}
