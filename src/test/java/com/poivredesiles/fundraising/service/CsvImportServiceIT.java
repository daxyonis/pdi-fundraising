package com.poivredesiles.fundraising.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.poivredesiles.fundraising.exception.PdiImportDataException;
import com.poivredesiles.fundraising.imports.CsvImportService;
import com.poivredesiles.fundraising.imports.dto.Campaign;
import com.poivredesiles.fundraising.imports.dto.Group;
import com.poivredesiles.fundraising.imports.dto.GroupLink;
import com.poivredesiles.fundraising.imports.dto.Product;
import com.poivredesiles.fundraising.imports.dto.Section;
import com.poivredesiles.fundraising.imports.dto.Seller;
import com.poivredesiles.fundraising.imports.dto.TypeBC;

public class CsvImportServiceIT {
	
	private static String FILEMAKER_CSV_FOLDER =  "src/test/resources/csv/";
	
	private final int NUM_SECTIONS = 16;
	private final int NUM_PRODUCTS = 99;	
	private final int NUM_TYPE_BC_PRODUCTS = 99;
	private final int NUM_CAMPAIGN = 2;
	private final int NUM_GROUPS = 6;
	private final int NUM_SELLERS = 28;
	
	Logger log = LoggerFactory.getLogger(CsvImportServiceIT.class);
		
	@BeforeAll
	public static void setup() {
		File file = new File(FILEMAKER_CSV_FOLDER);
		String absolutePath = file.getAbsolutePath();
		System.out.println(absolutePath);
		FILEMAKER_CSV_FOLDER = absolutePath + file.separator;
	}

	private CsvImportService csvImportService = new CsvImportService();
		
	@Test
	public void readSectionsTest() throws PdiImportDataException{
		log.info("---------- CSV Read Sections ------------");
		List<Section> sections = csvImportService.readSections(FILEMAKER_CSV_FOLDER + "section.csv");				
		log.info(sections.toString());
		assertEquals(NUM_SECTIONS, sections.size());
	}
	
	@Test
	public void readProductsTest() throws PdiImportDataException{
		log.info("---------- CSV Read Products ------------");
		List<Product> products = csvImportService.readProducts(FILEMAKER_CSV_FOLDER + "produit.csv");				
		log.info(products.toString());
		assertEquals(NUM_PRODUCTS, products.size());
	}
	
	@Test
	public void readTypeBCTest() throws PdiImportDataException{
		log.info("---------- CSV Read TypeBC ------------");
		List<TypeBC> typeBCs = csvImportService.readOrderTypes(FILEMAKER_CSV_FOLDER + "typebc.csv");				
		log.info(typeBCs.toString());
		assertEquals(NUM_TYPE_BC_PRODUCTS, typeBCs.size());
	}
	
	@Test
	public void readCampaignsTest() throws PdiImportDataException {
		log.info("---------- CSV Read Campaigns ------------");
		List<Campaign> campaigns = csvImportService.readCampaigns(FILEMAKER_CSV_FOLDER + "campagne.csv");				
		log.info(campaigns.toString());
		assertEquals(NUM_CAMPAIGN, campaigns.size());
	}
	
	@Test
	public void readGroupsTest() throws PdiImportDataException{
		log.info("---------- CSV Read Groups ------------");
		List<Group> groups = csvImportService.readGroups(FILEMAKER_CSV_FOLDER + "groupe.csv");				
		log.info(groups.toString());
		assertEquals(NUM_GROUPS, groups.size());
	}
	
	@Test
	public void readSellersTest() throws PdiImportDataException{
		log.info("---------- CSV Read Sellers ------------");
		List<Seller> sellers = csvImportService.readSellers(FILEMAKER_CSV_FOLDER + "vendeur.csv");				
		log.info(sellers.toString());
		assertEquals(NUM_SELLERS, sellers.size());
	}
	
	@Test
	public void readGroupLinksTest() throws PdiImportDataException{
		log.info("---------- CSV Read GroupLink ------------");
		List<GroupLink> groupLinks = csvImportService.readGroupLinks(FILEMAKER_CSV_FOLDER + "liengroupe.csv");				
		log.info(groupLinks.toString());
		assertEquals(NUM_SELLERS-1, groupLinks.size());
	}
}
