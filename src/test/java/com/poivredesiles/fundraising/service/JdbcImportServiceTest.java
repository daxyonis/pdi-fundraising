package com.poivredesiles.fundraising.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.poivredesiles.fundraising.jdbc.JdbcImportService;
import com.poivredesiles.fundraising.repository.product.PdiCategoryRepository;
import com.poivredesiles.fundraising.repository.product.PdiProductRepository;

public class JdbcImportServiceTest extends AbstractContainerBaseTest{
	
	private final Long NUM_SECTIONS = 16L;
	private final Long NUM_PRODUCTS = 99L;

	@Autowired
	private JdbcImportService jdbcImportService;
	
	@Autowired
	private PdiProductRepository pdiProductRepository;
	
	@Autowired
	private PdiCategoryRepository pdiCategoryRepository;
		
	
	@Test
	public void importSections() {
		jdbcImportService.importProductsAndSections();
		long numCategories = pdiCategoryRepository.count();
		long numProducts = pdiProductRepository.count();
		
		assertEquals(NUM_SECTIONS, numCategories);
		assertEquals(NUM_PRODUCTS, numProducts);
	}
}
