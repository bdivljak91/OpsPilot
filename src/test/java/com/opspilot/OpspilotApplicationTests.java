package com.opspilot;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class OpspilotApplicationTests {

	@Autowired
	private Environment environment;

	@Test
	void contextLoads() {
	}

	@Test
	void loadsTestProfileConfiguration() {
		assertThat(environment.getActiveProfiles()).containsExactly("test");
		assertThat(environment.getProperty("spring.application.name")).isEqualTo("opspilot");
		assertThat(environment.getProperty("logging.level.root")).isEqualTo("WARN");
	}

}
