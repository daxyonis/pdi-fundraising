package com.poivredesiles.fundraising.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.sql.DataSource;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.poivredesiles.fundraising.imports.CsvImportService;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
public class CsvImportServiceITest {	
	
	private final String FILEMAKER_CSV_FOLDER =  "C:\\Users\\evita\\OneDrive\\Documents\\Projects\\PoivreDesIles\\filemaker\\PDI\\";
	
	private final int NUM_SECTIONS = 16;
	private final int NUM_PRODUCTS = 99;
	private final int NUM_TYPE_BC = 1;
	private final int NUM_TYPE_BC_PRODUCTS = 99;
	private final int NUM_CAMPAIGN = 1;
	private final int NUM_GROUPS = 6;
	private final int NUM_SELLERS = 27;
	
	private JdbcTemplate jdbcTemplate;			
	
	private CsvImportService csvImportService;
	
	@Autowired
	public CsvImportServiceITest(DataSource datasource, CsvImportService csvImportService) {
		this.csvImportService = csvImportService;
		this.jdbcTemplate = new JdbcTemplate(datasource);
	}			
	
	@Test
	@Order(1)
	public void importSectionsTest() {
		csvImportService.importSections(FILEMAKER_CSV_FOLDER + "section.csv");
		int numCategories = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pdicategory", Integer.class);
		assertEquals(NUM_SECTIONS, numCategories);
	}
	
	@Test
	@Order(2)
	public void importProductsTest() {
		csvImportService.importProducts(FILEMAKER_CSV_FOLDER + "produit.csv");
		int numProducts = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pdiproduct", Integer.class);
		assertEquals(NUM_PRODUCTS, numProducts);
	}
	
	@Test
	@Order(3)
	public void importTypeBCTest() {
		csvImportService.importTypeBC(FILEMAKER_CSV_FOLDER + "typebc.csv");
		int numOrderTypes = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ordertype", Integer.class);		
		int numOrderTypePdiProducts = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ordertype_pdiproduct", Integer.class);
		assertEquals(NUM_TYPE_BC, numOrderTypes);
		assertEquals(NUM_TYPE_BC_PRODUCTS, numOrderTypePdiProducts);
	}
	
	@Test
	@Order(4)
	public void importCampaignsTest() {
		csvImportService.importCampaigns(FILEMAKER_CSV_FOLDER + "campagne.csv");
		int numCampaigns = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pdicampaign", Integer.class);
		assertEquals(NUM_CAMPAIGN, numCampaigns);
	}
	
	@Test
	@Order(5)
	public void importGroupsTest() {
		csvImportService.importGroups(FILEMAKER_CSV_FOLDER + "groupe.csv");
		int numGroups = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pdigroup", Integer.class);
		assertEquals(NUM_GROUPS,numGroups);
	}
	
	@Test
	@Order(6)
	public void importSellersTest() {
		csvImportService.importSellers(FILEMAKER_CSV_FOLDER + "vendeur.csv", FILEMAKER_CSV_FOLDER + "liengroupe.csv");
		int numSellers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pdiseller", Integer.class);
		int numUsers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user", Integer.class);
		assertEquals(NUM_SELLERS, numSellers);
		assertEquals(4, numUsers);	// 2 Admins + 1 buyer and 1 seller
	}
}
