package com.poivredesiles.fundraising.imports;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.poivredesiles.fundraising.exception.PdiImportDataException;
import com.poivredesiles.fundraising.imports.dto.Campaign;
import com.poivredesiles.fundraising.imports.dto.Group;
import com.poivredesiles.fundraising.imports.dto.Product;
import com.poivredesiles.fundraising.imports.dto.Section;
import com.poivredesiles.fundraising.imports.dto.Seller;
import com.poivredesiles.fundraising.imports.dto.TypeBC;
import com.poivredesiles.fundraising.service.OrderTypeService;
import com.poivredesiles.fundraising.service.PdiCampaignService;
import com.poivredesiles.fundraising.service.PdiCategoryService;
import com.poivredesiles.fundraising.service.PdiGroupService;
import com.poivredesiles.fundraising.service.PdiProductService;
import com.poivredesiles.fundraising.service.PdiSellerService;

@Service
public class CsvImportService {

	Logger log = LoggerFactory.getLogger(CsvImportService.class);
	
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
	 * CSV Import of Sections
	 * @param filename
	 */
	public void importSections(String filename) {
		List<Section> sections = readSections(filename);
		pdiCategoryService.importSections(sections);
	}
	
	/**
	 * CSV import of products
	 * @param filename
	 */
	public void importProducts(String filename) {
		List<Product> products = readProducts(filename);
		pdiProductService.importProducts(products);
	}
	
	
	/**
	 * CSV import of OrderType
	 * @param filename
	 */
	public void importTypeBC(String filename) {
		List<TypeBC> orderTypes = readOrderTypes(filename);
		orderTypeService.importOrderTypes(orderTypes);
	}
	
	/**
	 * CSV import of campaigns
	 * @param filename
	 */
	public void importCampaigns(String filename) {
		List<Campaign> campaigns = readCampaigns(filename);
		pdiCampaignService.importCampaigns(campaigns);
	}
	
	/**
	 * Read the section CSV file and build the object array
	 * Fields order:
	 * NoSection, PrixUnitaire, SectionEn, SectionFr
	 * @param filename
	 * @return a list of Section
	 */
	public List<Section> readSections(String filename) {
		List<Section> sections = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader(filename))) {
		      String[] lineInArray;
		      try {
				while ((lineInArray = reader.readNext()) != null) {
				    Section section = new Section();
				    section.setNumber(Long.parseLong(lineInArray[0]));					
				    section.setUnitPrice(BigDecimal.valueOf(Long.parseLong(lineInArray[1])));
				    section.setSectionEn(lineInArray[2]);
				    section.setSectionFr(lineInArray[3]);
				    sections.add(section);
				  }
			} catch (CsvValidationException | IOException e) {
				log.error("Error while reading csv file", e);
				throw new PdiImportDataException("Could not import section csv data !");
			}
		  } catch (IOException e1) {
			log.error("Could not open file " + filename, e1);
			throw new PdiImportDataException("Could not open section csv file !");
		  }
		return sections;
	}
	
	/**
	 * Read the product CSV file and build the object array
	 * Field order:
	 * DescriptionEn, DescriptionFr, NoEtiquette, NomEn, NomFr, NoProduit, NoSection, Poids
	 * @param filename
	 * @return a list of Product
	 */
	public List<Product> readProducts(String filename) {
		List<Product> products = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader(filename))) {
		      String[] lineInArray;
		      try {
				while ((lineInArray = reader.readNext()) != null) {
				    Product product = new Product();
				    product.setDescEn(lineInArray[0]);
				    product.setDescFr(lineInArray[1]);
				    product.setLabelNumber(lineInArray[2]);
				    product.setNameEn(lineInArray[3]);
				    product.setNameFr(lineInArray[4]);
				    product.setNumber(lineInArray[5]);
				    product.setSectionNum(Long.parseLong(lineInArray[6]));
				    product.setWeight(lineInArray[7]);
				    products.add(product);
				  }
			} catch (CsvValidationException | IOException e) {
				log.error("Error while reading csv file", e);
				throw new PdiImportDataException("Could not import product csv data !");
			}
		  } catch (IOException e1) {
			log.error("Could not open file " + filename, e1);
			throw new PdiImportDataException("Could not open product csv file !");
		  }
		return products;
	}
	
	/**
	 * Read the TypeBC CSV file and build the object array
	 * Field order: NoProduit, NoTypeBC
	 * @param filename
	 * @return a list of TypeBC
	 */
	public List<TypeBC> readOrderTypes(String filename) {
		List<TypeBC> orderTypes = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader(filename))) {
		      String[] lineInArray;
		      try {
				while ((lineInArray = reader.readNext()) != null) {
					TypeBC orderType = new TypeBC();
					orderType.setProductNumber(lineInArray[0]);
					orderType.setNumber(Long.parseLong(lineInArray[1]));				    
				    orderTypes.add(orderType);
				  }
			} catch (CsvValidationException | IOException e) {
				log.error("Error while reading csv file", e);
				throw new PdiImportDataException("Could not import typeBC csv data !");
			}
		  } catch (IOException e1) {
			log.error("Could not open file " + filename, e1);
			throw new PdiImportDataException("Could not open typeBC csv file !");
		  }
		return orderTypes;
	}
	
	/**
	 * Read the campaign CSV file and build the object array
	 * Field order: Bloque, CourrielResponsable, DateLimite, DateTerminee, NoCampagne, NomOrganisme, NoOrganisme, NoResponsable, NoTypeBC, Projet
	 * @param filename
	 * @return a list of campaigns
	 */
	public List<Campaign> readCampaigns(String filename){
		List<Campaign> campaigns = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader(filename))) {
		      String[] lineInArray;
		      
		      try {
				while ((lineInArray = reader.readNext()) != null) {
					Campaign campaign = new Campaign();					
					campaign.setBlockedAsString(lineInArray[0]);
					campaign.setLeaderEmail(lineInArray[1]);
					campaign.setDueDate(ImportsUtils.parseDate(lineInArray[2], new SimpleDateFormat("dd/M/yyyy")));
					campaign.setClosedDate(ImportsUtils.parseDate(lineInArray[3], new SimpleDateFormat("dd/M/yyyy")));
					campaign.setNumber(Long.valueOf(lineInArray[4]));
					campaign.setOrganizationName(lineInArray[5]);
					campaign.setOrganizationNumber(lineInArray[6]);
					campaign.setLeaderNumber(lineInArray[7]);
					campaign.setNumTypeBC(Long.valueOf(lineInArray[8]));
					campaign.setProject(lineInArray[9]);
					campaigns.add(campaign);
				  }
			} catch (CsvValidationException | IOException | ParseException e) {
				log.error("Error while reading csv file", e);
				throw new PdiImportDataException("Could not import campaign csv data !");
			}
		  } catch (IOException e1) {
			log.error("Could not open file " + filename, e1);
			throw new PdiImportDataException("Could not open campaign csv file !");
		  }
		return campaigns;
	}
	
	/**
	 * Read the group CSV file and build the object array
	 * Field order: Groupe, NoCampagne, NoGroupe, NoResponsable
	 * @param filename
	 * @return
	 */
	public List<Group> readGroups(String filename){
		List<Group> groups = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader(filename))) {
		      String[] lineInArray;
		      try {
				while ((lineInArray = reader.readNext()) != null) {
					Group group = new Group();
					group.setName(lineInArray[0]);
					group.setCampaignNumber(Long.parseLong(lineInArray[1]));
					group.setNumber(Long.parseLong(lineInArray[2]));
					group.setLeaderNumber(lineInArray[3]);
					groups.add(group);
				  }
			} catch (CsvValidationException | IOException e) {
				log.error("Error while reading csv file", e);
				throw new PdiImportDataException("Could not import group csv data !");
			}
		  } catch (IOException e1) {
			log.error("Could not open file " + filename, e1);
			throw new PdiImportDataException("Could not open group csv file !");
		  }
		return groups;
	}
	
	/**
	 * Read the seller CSV file and build the object array
	 * Field order: Autorisation, CodeAcheteur, CodeCampagne, MotDePasse, NomVendeur, NoVendeur 
	 * @param filename
	 * @return
	 */
	public List<Seller> readSellers(String filename){
		List<Seller> sellers = new ArrayList<>();		
		try (CSVReader reader = new CSVReader(new FileReader(filename))) {
		      String[] lineInArray;
		      try {
				while ((lineInArray = reader.readNext()) != null) {
					Seller seller = new Seller();
					seller.setAuthorization(lineInArray[0]);
					seller.setBuyerCode(lineInArray[1]);
					seller.setCampaignCode(lineInArray[2]);
					seller.setPassword(lineInArray[3]);
					seller.setName(lineInArray[4]);
					seller.setNumber(Long.parseLong(lineInArray[5]));
				    sellers.add(seller);
				  }
			} catch (CsvValidationException | IOException e) {
				log.error("Error while reading csv file", e);
				throw new PdiImportDataException("Could not import seller csv data !");
			}
		  } catch (IOException e1) {
			log.error("Could not open file " + filename, e1);
			throw new PdiImportDataException("Could not open seller csv file !");
		  }
		return sellers;
	}
}
