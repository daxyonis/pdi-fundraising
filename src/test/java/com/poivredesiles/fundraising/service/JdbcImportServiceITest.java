package com.poivredesiles.fundraising.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.poivredesiles.fundraising.jdbc.JdbcImportService;

@SpringBootTest
@ActiveProfiles("test")
public class JdbcImportServiceITest{
	
	private final int NUM_SECTIONS = 16;
	private final int NUM_PRODUCTS = 99;
	private final int NUM_TYPE_BC = 1;
	private final int NUM_TYPE_BC_PRODUCTS = 99;
	
	private JdbcTemplate jdbcTemplate;			
	
	private JdbcImportService jdbcImportService;
	
	@Autowired
	public JdbcImportServiceITest(DataSource datasource, JdbcImportService jdbcImportService) {
		this.jdbcImportService = jdbcImportService;
		this.jdbcTemplate = new JdbcTemplate(datasource);
	}			
		
	@Test
	public void importProductsAndSectionsTest() throws SQLException {
		jdbcImportService.importProductsAndSections();		
					
		int numCategories = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pdicategory", Integer.class);
		int numProducts = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pdiproduct", Integer.class);
		int numOrderTypes = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ordertype", Integer.class);
		int numOrderTypePdiProducts = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ordertype_pdiproduct", Integer.class);
		
		assertEquals(NUM_SECTIONS, numCategories);
		assertEquals(NUM_PRODUCTS, numProducts);
		assertEquals(NUM_TYPE_BC, numOrderTypes);
		assertEquals(NUM_TYPE_BC_PRODUCTS, numOrderTypePdiProducts);								
	}
	
	@Test
	public void importCampaignGroupsAndSellersTest() {
		jdbcImportService.importCampaignGroupsAndSellers();
	}
}
