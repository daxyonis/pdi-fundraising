package com.poivredesiles.fundraising.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import nz.net.ultraq.thymeleaf.LayoutDialect;

@Configuration
public class AppConfig {

	/**
	 * Thymeleaf Layout Dialect to use layouts
	 * @return
	 */
	@Bean
	public LayoutDialect layoutDialect() {
		return new LayoutDialect();
	}
}
