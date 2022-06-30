package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootTest
class FilmorateApplicationTests {
	ConfigurableApplicationContext application;

	@BeforeEach
	private void start() {
		application = SpringApplication.run(FilmorateApplication.class);
	}

	@AfterEach
	private void stop() {
		int exitCode = SpringApplication.exit(application, () -> 0);
		System.exit(exitCode);
	}

	@Test
	void UserCreateFailLogin() {

	}

}
