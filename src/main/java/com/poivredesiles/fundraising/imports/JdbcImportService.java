package com.poivredesiles.fundraising.imports;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.poivredesiles.fundraising.imports.dto.Campaign;
import com.poivredesiles.fundraising.imports.dto.Group;
import com.poivredesiles.fundraising.imports.dto.GroupLink;
import com.poivredesiles.fundraising.imports.dto.Product;
import com.poivredesiles.fundraising.imports.dto.Section;
import com.poivredesiles.fundraising.imports.dto.Seller;
import com.poivredesiles.fundraising.imports.dto.TypeBC;
import com.poivredesiles.fundraising.imports.mapper.CampaignRowMapper;
import com.poivredesiles.fundraising.imports.mapper.GroupLinkRowMapper;
import com.poivredesiles.fundraising.imports.mapper.GroupRowMapper;
import com.poivredesiles.fundraising.imports.mapper.ProductRowMapper;
import com.poivredesiles.fundraising.imports.mapper.SectionRowMapper;
import com.poivredesiles.fundraising.imports.mapper.SellerRowMapper;
import com.poivredesiles.fundraising.imports.mapper.TypeBCRowMapper;
import com.poivredesiles.fundraising.service.OrderTypeService;
import com.poivredesiles.fundraising.service.PdiCampaignService;
import com.poivredesiles.fundraising.service.PdiCategoryService;
import com.poivredesiles.fundraising.service.PdiGroupService;
import com.poivredesiles.fundraising.service.PdiProductService;
import com.poivredesiles.fundraising.service.PdiSellerService;

@Service
public class JdbcImportService {

	Logger log = LoggerFactory.getLogger(JdbcImportService.class);

	@Autowired	
	private FileMakerDatasource fileMakerDatasource;
	
	@Autowired
	private PdiCategoryService pdiCategoryService;
	
	@Autowired
	private PdiProductService pdiProductService;
	
	@Autowired
	private OrderTypeService orderTypeService;
	
	@Autowired
	private PdiCampaignService pdiCampaignService;
	
	@Autowired
	private PdiGroupService pdiGroupService;
	
	@Autowired
	private PdiSellerService pdiSellerService;

	/**
	 * JDBC IMPORT of Products and Sections
	 */
	public void importProductsAndSections() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(fileMakerDatasource.create());

		List<Section> sections = readSections(jdbcTemplate);
		pdiCategoryService.importSections(sections);
		
		List<Product> products = readProducts(jdbcTemplate);
		pdiProductService.importProducts(products);
		
		List<TypeBC> orderTypes = readOrderTypes(jdbcTemplate);
		orderTypeService.importOrderTypes(orderTypes);
	}
	
	/**
	 * JDBC IMPORT of Campaigns, Groups and Sellers 
	 */
	public void importCampaignGroupsAndSellers() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(fileMakerDatasource.create());
		
		List<Campaign> campaigns = readCampaigns(jdbcTemplate);
		pdiCampaignService.importCampaigns(campaigns);
		
		List<Group> groups = readGroups(jdbcTemplate);
		pdiGroupService.importGroups(groups);
		
		List<GroupLink> groupLinks = readGroupLinks(jdbcTemplate);
		List<Seller> sellers = readSellers(jdbcTemplate);
		pdiSellerService.importSellers(sellers, groupLinks);
	}	

	/**
	 * Import the order type data
	 * @param jdbcTemplate
	 * @return
	 */
	private List<TypeBC> readOrderTypes(JdbcTemplate jdbcTemplate) {
		assert (jdbcTemplate != null);
		try {
			List<TypeBC> orderTypes = jdbcTemplate.query("select * from TypeBC", new TypeBCRowMapper());
			return orderTypes;
		} catch (DataAccessException e) {
			log.error("Error reading the FM TypeBC", e);
			throw e;
		}
	}

	/**
	 * Import the product data
	 */
	private List<Product> readProducts(JdbcTemplate jdbcTemplate) {
		assert (jdbcTemplate != null);
		try {
			List<Product> products = jdbcTemplate.query("select * from Produit", new ProductRowMapper());
			return products;
		} catch (DataAccessException e) {
			log.error("Error reading the FM Produit", e);
			throw e;
		}
	}

	/**
	 * Import the section data
	 */
	private List<Section> readSections(JdbcTemplate jdbcTemplate) {
		assert (jdbcTemplate != null);		
		try {
			// Must put the "Section" table name in quotes because it is a reserved word in SQL
			List<Section> sections = jdbcTemplate.query("select * from \"Section\"", new SectionRowMapper());
			return sections;
		} catch (DataAccessException e) {
			log.error("Error reading the FM Section", e);
			throw e;
		}
	}

	/**
	 * Import the Campaign data
	 * @param jdbcTemplate
	 * @return a list of campaigns
	 */
	private List<Campaign> readCampaigns(JdbcTemplate jdbcTemplate) {
		assert (jdbcTemplate != null);
		try {
			List<Campaign> campaigns = jdbcTemplate.query("select * from Campagne", new CampaignRowMapper());
			return campaigns;
		} catch (DataAccessException e) {
			log.error("Error reading the FM Campagne", e);
			throw e;
		}
	}
	
	/**
	 * Import the Group data
	 * @param jdbcTemplate
	 * @return a list of groups
	 */
	private List<Group> readGroups(JdbcTemplate jdbcTemplate) {
		assert (jdbcTemplate != null);
		try {
			List<Group> groups = jdbcTemplate.query("select * from Groupe", new GroupRowMapper());
			return groups;
		} catch (DataAccessException e) {
			log.error("Error reading the FM Groupe", e);
			throw e;
		}
	}
	
	/**
	 * Import the Seller data
	 * @param jdbcTemplate
	 * @return a list of sellers
	 */
	private List<Seller> readSellers(JdbcTemplate jdbcTemplate) {
		assert (jdbcTemplate != null);
		try {
			List<Seller> sellers = jdbcTemplate.query("select * from Vendeur", new SellerRowMapper());
			return sellers;
		} catch (DataAccessException e) {
			log.error("Error reading the FM Vendeur", e);
			throw e;
		}
	}

	/**
	 * Import the GroupLink data
	 * @param jdbcTemplate
	 * @return a list of GroupLinks
	 */
	private List<GroupLink> readGroupLinks(JdbcTemplate jdbcTemplate) {
		assert (jdbcTemplate != null);
		try {
			List<GroupLink> groupLinks = jdbcTemplate.query("select * from LienGroupe", new GroupLinkRowMapper());
			return groupLinks;
		} catch (DataAccessException e) {
			log.error("Error reading the FM LienGroupe", e);
			throw e;
		}
	}

	

	
}
