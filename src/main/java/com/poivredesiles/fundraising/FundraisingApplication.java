package com.poivredesiles.fundraising;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class FundraisingApplication {

	public static void main(String[] args) {
		SpringApplication.run(FundraisingApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry
						.addMapping("/api/global/response")
						.allowedOrigins("https://pay.sandbox.realexpayments.com", "https://pay.realexpayments.com");
			}
		};
	}
}
