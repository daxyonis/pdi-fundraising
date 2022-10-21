package com.poivredesiles.fundraising.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.poivredesiles.fundraising.exception.PdiImportDataException;
import com.poivredesiles.fundraising.imports.CsvImportService;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
public class CsvImportServiceIT2 {	
	
	private static String FILEMAKER_CSV_FOLDER =  "src/test/resources/csv/";
	
	private final int NUM_SECTIONS = 16;
	private final int NUM_PRODUCTS = 99;
	private final int NUM_TYPE_BC = 1;
	private final int NUM_TYPE_BC_PRODUCTS = 99;
	private final int NUM_CAMPAIGN = 2;
	private final int NUM_GROUPS = 6;
	private final int NUM_SELLERS = 27;
	
	private JdbcTemplate jdbcTemplate;			
	
	private CsvImportService csvImportService;
	
	@BeforeAll
	public static void setup() {
		File file = new File(FILEMAKER_CSV_FOLDER);
		String absolutePath = file.getAbsolutePath();
		System.out.println(absolutePath);
		FILEMAKER_CSV_FOLDER = absolutePath + file.separator;
	}
	
	@Autowired
	public CsvImportServiceIT2(DataSource datasource, CsvImportService csvImportService) {
		this.csvImportService = csvImportService;
		this.jdbcTemplate = new JdbcTemplate(datasource);
	}			
	
	@Test
	@Order(1)
	public void importSectionsTest() throws PdiImportDataException {
		csvImportService.importSections(FILEMAKER_CSV_FOLDER + "section.csv");
		int numCategories = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pdicategory", Integer.class);
		assertEquals(NUM_SECTIONS, numCategories);
	}
	
	@Test
	@Order(2)
	public void importProductsTest() throws PdiImportDataException {
		csvImportService.importProducts(FILEMAKER_CSV_FOLDER + "produit.csv");
		int numProducts = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pdiproduct", Integer.class);
		assertEquals(NUM_PRODUCTS, numProducts);
	}
	
	@Test
	@Order(3)
	public void importTypeBCTest() throws PdiImportDataException {
		csvImportService.importTypeBC(FILEMAKER_CSV_FOLDER + "typebc.csv");
		int numOrderTypes = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ordertype", Integer.class);		
		int numOrderTypePdiProducts = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ordertype_pdiproduct", Integer.class);
		assertEquals(NUM_TYPE_BC, numOrderTypes);
		assertEquals(NUM_TYPE_BC_PRODUCTS, numOrderTypePdiProducts);
	}
	
	@Test
	@Order(4)
	public void importCampaignsTest() throws PdiImportDataException {
		csvImportService.importCampaigns(FILEMAKER_CSV_FOLDER + "campagne.csv");
		int numCampaigns = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pdicampaign", Integer.class);
		assertEquals(NUM_CAMPAIGN, numCampaigns);
	}
	
	@Test
	@Order(5)
	public void importGroupsTest() throws PdiImportDataException {
		csvImportService.importGroups(FILEMAKER_CSV_FOLDER + "groupe.csv");
		int numGroups = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pdigroup", Integer.class);
		assertEquals(NUM_GROUPS,numGroups);
	}
	
	@Test
	@Order(6)
	public void importSellersTest() throws PdiImportDataException {
		csvImportService.importSellers(FILEMAKER_CSV_FOLDER + "vendeur.csv");
		int numSellers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pdiseller where pdi_group_id is null", Integer.class);
		int numUsers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user", Integer.class);
		assertEquals(NUM_SELLERS, numSellers);
		assertEquals(3, numUsers);	// 1 Admin + 1 buyer and 1 seller
	}
	
	@Test
	@Order(7)
	public void importGroupLinksTest() throws PdiImportDataException {
		csvImportService.importGroupLinks(FILEMAKER_CSV_FOLDER + "liengroupe.csv");
		int numLinkedSellers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pdiseller where pdi_group_id is not null", Integer.class);
		assertEquals(NUM_SELLERS, numLinkedSellers);
	}
}
