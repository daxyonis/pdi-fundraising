package com.poivredesiles.fundraising.service;

import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest
@ContextConfiguration(initializers = { AbstractContainerBaseTest.Initializer.class })
public abstract class AbstractContainerBaseTest {

	@ClassRule
	public static MySQLContainer mysql = new MySQLContainer("mysql")
			.withDatabaseName("pdi_test")
			.withUsername("integrationUser")
			.withPassword("testPass");
	
	@BeforeAll
	public static void start() {
		mysql.start();
	}
	
	@AfterAll
	public static void stop() {
		mysql.stop();
	}

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
			TestPropertyValues
					.of("spring.datasource.url=" + mysql.getJdbcUrl(),
							"spring.datasource.username=" + mysql.getUsername(),
							"spring.datasource.password=" + mysql.getPassword())
					.applyTo(configurableApplicationContext.getEnvironment());
		}
	}
}
