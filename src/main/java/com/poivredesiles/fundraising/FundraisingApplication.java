package com.poivredesiles.fundraising;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FundraisingApplication {

	public static void main(String[] args) {
		SpringApplication.run(FundraisingApplication.class, args);
	}

}
