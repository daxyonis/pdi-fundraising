package com.poivredesiles.fundraising.config;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
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
