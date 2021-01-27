package com.poivredesiles.fundraising.jdbc;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.poivredesiles.fundraising.jdbc.dto.Product;
import com.poivredesiles.fundraising.jdbc.dto.Section;
import com.poivredesiles.fundraising.jdbc.dto.TypeBC;
import com.poivredesiles.fundraising.jdbc.mapper.ProductRowMapper;
import com.poivredesiles.fundraising.jdbc.mapper.SectionRowMapper;
import com.poivredesiles.fundraising.jdbc.mapper.TypeBCRowMapper;
import com.poivredesiles.fundraising.service.OrderTypeService;
import com.poivredesiles.fundraising.service.PdiCategoryService;
import com.poivredesiles.fundraising.service.PdiProductService;

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

	/**
	 * IMPORT of Products and Sections
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

}
