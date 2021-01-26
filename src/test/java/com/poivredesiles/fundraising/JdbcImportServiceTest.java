package com.poivredesiles.fundraising;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.poivredesiles.fundraising.jdbc.JdbcImportService;

@SpringBootTest
public class JdbcImportServiceTest {

	@Autowired
	private JdbcImportService jdbcImportService;
	
	@Test
	public void importSections() {
		jdbcImportService.importProductsAndSections();
	}
}
