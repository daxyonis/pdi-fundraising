package com.poivredesiles.fundraising.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.poivredesiles.fundraising.jdbc.JdbcImportService;
import com.poivredesiles.fundraising.repository.order.OrderTypeRepository;
import com.poivredesiles.fundraising.repository.product.PdiCategoryRepository;
import com.poivredesiles.fundraising.repository.product.PdiProductRepository;

public class JdbcImportServiceITest extends AbstractContainerBaseTest{
	
	private final Long NUM_SECTIONS = 16L;
	private final Long NUM_PRODUCTS = 99L;
	private final Long NUM_TYPE_BC = 1L;
	private final int NUM_TYPE_BC_PRODUCTS = 99;

	@Autowired
	private JdbcImportService jdbcImportService;
	
	@Autowired
	private PdiProductRepository pdiProductRepository;
	
	@Autowired
	private PdiCategoryRepository pdiCategoryRepository;
	
	@Autowired
	private OrderTypeRepository orderTypeRepository;
	
	@Test
	public void importSections() throws SQLException {
		jdbcImportService.importProductsAndSections();
		long numCategories = pdiCategoryRepository.count();
		long numProducts = pdiProductRepository.count();
		long numOrderTypes = orderTypeRepository.count();
		
		// Check the count of each entity table
		assertEquals(NUM_SECTIONS, numCategories);
		assertEquals(NUM_PRODUCTS, numProducts);
		assertEquals(NUM_TYPE_BC, numOrderTypes);
		
		// Check the count of the join table
		ResultSet resultSet = performQuery(mysql, "SELECT COUNT(*) FROM ordertype_pdiproduct");
		int resultSetInt = resultSet.getInt(1);
		
		assertEquals(NUM_TYPE_BC_PRODUCTS, resultSetInt);		
	}
}
