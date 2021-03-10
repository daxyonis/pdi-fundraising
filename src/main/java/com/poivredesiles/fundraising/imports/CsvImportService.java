package com.poivredesiles.fundraising.imports;

import static com.poivredesiles.fundraising.imports.ImportsUtils.sanitize;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.poivredesiles.fundraising.exception.PdiImportDataException;
import com.poivredesiles.fundraising.imports.ImportsUtils.DataTypeEnum;
import com.poivredesiles.fundraising.imports.dto.Campaign;
import com.poivredesiles.fundraising.imports.dto.Group;
import com.poivredesiles.fundraising.imports.dto.GroupLink;
import com.poivredesiles.fundraising.imports.dto.Product;
import com.poivredesiles.fundraising.imports.dto.Section;
import com.poivredesiles.fundraising.imports.dto.Seller;
import com.poivredesiles.fundraising.imports.dto.TypeBC;
import com.poivredesiles.fundraising.repository.group.GroupLastImportRepository;
import com.poivredesiles.fundraising.repository.product.ProductLastImportRepository;
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

	@Autowired
	private ProductLastImportRepository productLastImportRepository;

	@Autowired
	private GroupLastImportRepository groupLastImportRepository;

	/**
	 * Dispatch import call
	 * 
	 * @param file     the file to import
	 * @param dataType the type of data
	 * @return the last import date
	 * @throws PdiImportDataException 
	 */
	public String dispatchImport(MultipartFile file, DataTypeEnum dataType) throws PdiImportDataException {
		switch (dataType) {
		case SECTION:
			return importSections(file);
		case PRODUCT:
			return importProducts(file);
		case ORDERTYPE:
			return importTypeBC(file);
		case CAMPAIGN:
			return importCampaigns(file);
		case GROUP:
			return importGroups(file);
		case SELLER:
			return importSellers(file);
		case GROUPLINK:
			return importGroupLinks(file);
		default:
			throw new IllegalArgumentException("Unsupported file type");
		}
	}

	/**
	 * CSV Import of Sections
	 * 
	 * @param filename
	 * @throws PdiImportDataException 
	 */
	public void importSections(String filename) throws PdiImportDataException {
		List<Section> sections = readSections(filename);
		pdiCategoryService.importSections(sections);
	}

	private String importSections(MultipartFile file) throws PdiImportDataException {
		List<Section> sections = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {
			readAndfillSections(sections, reader);
		} catch (IOException e1) {
			log.error("Could not open file " + file.getName(), e1);
			throw new PdiImportDataException("Erreur d'ouverture du fichier CSV pour les sections.");
		}
		pdiCategoryService.importSections(sections);
		return this.getSectionsAndProductsLastImportDate();
	}

	/**
	 * CSV import of products
	 * 
	 * @param filename
	 * @throws PdiImportDataException 
	 */
	public void importProducts(String filename) throws PdiImportDataException {
		List<Product> products = readProducts(filename);
		pdiProductService.importProducts(products);
	}

	private String importProducts(MultipartFile file) throws PdiImportDataException {
		List<Product> products = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {
			readAndFillProducts(products, reader);
		} catch (IOException e1) {
			log.error("Could not open file " + file.getName(), e1);
			throw new PdiImportDataException("Erreur d'ouverture du fichier CSV pour les produits.");
		}
		pdiProductService.importProducts(products);
		return this.getSectionsAndProductsLastImportDate();
	}

	/**
	 * CSV import of OrderType
	 * 
	 * @param filename
	 * @throws PdiImportDataException 
	 */
	public void importTypeBC(String filename) throws PdiImportDataException {
		List<TypeBC> orderTypes = readOrderTypes(filename);
		orderTypeService.importOrderTypes(orderTypes);
	}

	private String importTypeBC(MultipartFile file) throws PdiImportDataException {
		List<TypeBC> orderTypes = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {
			readAndFillOrderTypes(orderTypes, reader);
		} catch (IOException e1) {
			log.error("Could not open file " + file.getName(), e1);
			throw new PdiImportDataException("Erreur d'ouverture du fichier CSV pour les TypeBC");
		}
		orderTypeService.importOrderTypes(orderTypes);
		return this.getSectionsAndProductsLastImportDate();
	}

	/**
	 * CSV import of campaigns
	 * 
	 * @param filename
	 * @throws PdiImportDataException 
	 */
	public void importCampaigns(String filename) throws PdiImportDataException {
		List<Campaign> campaigns = readCampaigns(filename);
		pdiCampaignService.importCampaigns(campaigns);
	}

	private String importCampaigns(MultipartFile file) throws PdiImportDataException {
		List<Campaign> campaigns = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {
			readAndFillCampaigns(campaigns, reader);
		} catch (IOException e1) {
			log.error("Could not open file " + file.getName(), e1);
			throw new PdiImportDataException("Erreur d'ouverture du fichier CSV pour les campagnes.");
		}
		pdiCampaignService.importCampaigns(campaigns);
		return this.getGroupsAndSellersLastImportDate();
	}

	/**
	 * CSV import of groups
	 * 
	 * @param filename
	 * @throws PdiImportDataException 
	 */
	public void importGroups(String filename) throws PdiImportDataException {
		List<Group> groups = readGroups(filename);
		pdiGroupService.importGroups(groups);
	}

	private String importGroups(MultipartFile file) throws PdiImportDataException {
		List<Group> groups = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {
			readAndFillGroups(groups, reader);
		} catch (IOException e1) {
			log.error("Could not open file " + file.getName(), e1);
			throw new PdiImportDataException("Erreur d'ouverture du fichier CSV pour les groupes.");
		}
		pdiGroupService.importGroups(groups);
		return this.getGroupsAndSellersLastImportDate();
	}

	/**
	 * CSV import of sellers
	 * 
	 * @param sellersFilename
	 * @throws PdiImportDataException 
	 */
	public void importSellers(String sellersFilename) throws PdiImportDataException {
		List<Seller> sellers = readSellers(sellersFilename);
		pdiSellerService.importSellers(sellers);
	}

	private String importSellers(MultipartFile file) throws PdiImportDataException {
		List<Seller> sellers = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {
			readAndFillSellers(sellers, reader);
		} catch (IOException e1) {
			log.error("Could not open file " + file.getName(), e1);
			throw new PdiImportDataException("Erreur d'ouverture du fichier CSV pour les vendeurs");
		}
		pdiSellerService.importSellers(sellers);
		return this.getGroupsAndSellersLastImportDate();
	}

	/**
	 * CSV import of group links
	 * 
	 * @param groupLinksFilename
	 * @throws PdiImportDataException 
	 */
	public void importGroupLinks(String groupLinksFilename) throws PdiImportDataException {
		List<GroupLink> groupLinks = readGroupLinks(groupLinksFilename);
		pdiSellerService.linkSellersToGroup(groupLinks);
	}

	private String importGroupLinks(MultipartFile file) throws PdiImportDataException {
		List<GroupLink> groupLinks = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {
			readAndFillGroupLinks(groupLinks, reader);
		} catch (IOException e1) {
			log.error("Could not open file " + file.getName(), e1);
			throw new PdiImportDataException("Erreur d'ouverture du fichier CSV pour les liens de groupe.");
		}
		pdiSellerService.linkSellersToGroup(groupLinks);
		return this.getGroupsAndSellersLastImportDate();
	}

	/**
	 * Read the section CSV file and build the object array Fields order: NoSection,
	 * PrixUnitaire, SectionEn, SectionFr
	 * 
	 * @param filename
	 * @return a list of Section
	 * @throws PdiImportDataException
	 */
	public List<Section> readSections(String filename) throws PdiImportDataException {
		List<Section> sections = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader(filename))) {
			readAndfillSections(sections, reader);
		} catch (IOException e1) {
			log.error("Could not open file " + filename, e1);
			throw new PdiImportDataException("Could not open section csv file !");
		}
		return sections;
	}

	private void readAndfillSections(List<Section> sections, CSVReader reader) throws PdiImportDataException {
		String[] lineInArray;
		try {
			double numFaultyLines = 0.0;			
			while ((lineInArray = reader.readNext()) != null) {
				try {
					Section section = new Section();
					section.setNumber(Long.parseLong(lineInArray[0]));
					section.setUnitPrice(BigDecimal.valueOf(Long.parseLong(lineInArray[1])));
					section.setSectionEn(lineInArray[2]);
					section.setSectionFr(lineInArray[3]);
					sections.add(section);
				} catch (NumberFormatException e1) {
					numFaultyLines+=1.0;
					log.error("Error while reading csv file", e1);
					continue;
				}
			}
			if((numFaultyLines / reader.getLinesRead()) > 0.1) {
				throw new PdiImportDataException("Plus de 10% des données du fichier présentent une erreur de lecture.");
			}
		} catch (CsvValidationException | IOException e) {
			log.error("Error while reading csv file", e);	
			throw new PdiImportDataException("Erreur de lecture des sections - verifier le fichier.");
		}

	}

	/**
	 * Read the product CSV file and build the object array Field order:
	 * DescriptionEn, DescriptionFr, NoEtiquette, NomEn, NomFr, NoProduit,
	 * NoSection, Poids
	 * 
	 * @param filename
	 * @return a list of Product
	 * @throws PdiImportDataException 
	 */
	public List<Product> readProducts(String filename) throws PdiImportDataException {
		List<Product> products = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader(filename))) {
			readAndFillProducts(products, reader);
		} catch (IOException e1) {
			log.error("Could not open file " + filename, e1);
			throw new PdiImportDataException("Could not open product csv file !");
		}
		return products;
	}

	private void readAndFillProducts(List<Product> products, CSVReader reader) throws PdiImportDataException {
		String[] lineInArray;
		try {
			double numFaultyLines = 0.0;
			while ((lineInArray = reader.readNext()) != null) {
				try {
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
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e1) {
					numFaultyLines += 1.0;
					log.error("Error while reading csv file", e1);
					continue;
				}
			}
			if((numFaultyLines / reader.getLinesRead()) > 0.1) {
				throw new PdiImportDataException("Plus de 10% des données du fichier présentent une erreur de lecture.");
			}
		} catch (CsvValidationException | IOException e) {
			log.error("Error while reading csv file", e);
			throw new PdiImportDataException("Erreur de lecture des produits - verifier le fichier.");
		}
	}

	/**
	 * Read the TypeBC CSV file and build the object array Field order: NoProduit,
	 * NoTypeBC
	 * 
	 * @param filename
	 * @return a list of TypeBC
	 * @throws PdiImportDataException 
	 */
	public List<TypeBC> readOrderTypes(String filename) throws PdiImportDataException {
		List<TypeBC> orderTypes = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader(filename))) {
			readAndFillOrderTypes(orderTypes, reader);
		} catch (IOException e1) {
			log.error("Could not open file " + filename, e1);
			throw new PdiImportDataException("Could not open typeBC csv file !");
		}
		return orderTypes;
	}

	private void readAndFillOrderTypes(List<TypeBC> orderTypes, CSVReader reader) throws PdiImportDataException {
		String[] lineInArray;
		try {
			
			double numFaultyLines = 0.0;
			while ((lineInArray = reader.readNext()) != null) {
				try {
					TypeBC orderType = new TypeBC();
					orderType.setProductNumber(lineInArray[0]);
					orderType.setNumber(Long.parseLong(lineInArray[1]));
					orderTypes.add(orderType);
				} catch (NumberFormatException e1) {
					numFaultyLines += 1.0;
					log.error("Error while reading csv file", e1);
					continue;
				}
			}
			if((numFaultyLines / reader.getLinesRead()) > 0.1) {
				throw new PdiImportDataException("Plus de 10% des données du fichier présentent une erreur de lecture.");
			}
		} catch (CsvValidationException | IOException e) {
			log.error("Error while reading csv file", e);
			throw new PdiImportDataException("Erreur de lecture des type BC - verifier le fichier.");
		}
	}

	/**
	 * Read the campaign CSV file and build the object array Field order: Bloque,
	 * CourrielResponsable, DateLimite, DateTerminee, NoCampagne, NomOrganisme,
	 * NoOrganisme, NoResponsable, NoTypeBC, Projet
	 * 
	 * @param filename
	 * @return a list of campaigns
	 * @throws PdiImportDataException 
	 */
	public List<Campaign> readCampaigns(String filename) throws PdiImportDataException {
		List<Campaign> campaigns = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader(filename))) {
			readAndFillCampaigns(campaigns, reader);
		} catch (IOException e1) {
			log.error("Could not open file " + filename, e1);
			throw new PdiImportDataException("Could not open campaign csv file !");
		}
		return campaigns;
	}

	private void readAndFillCampaigns(List<Campaign> campaigns, CSVReader reader) throws PdiImportDataException {
		String[] lineInArray;

		try {
			double numFaultyLines = 0.0;
			while ((lineInArray = reader.readNext()) != null) {
				try {
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
				} catch (NumberFormatException e1) {
					numFaultyLines += 1.0;
					log.error("Error while reading csv file", e1);
					continue;
				}				
			}
			if((numFaultyLines / reader.getLinesRead()) > 0.1) {
				throw new PdiImportDataException("Plus de 10% des données du fichier présentent une erreur de lecture.");
			}
		} catch (CsvValidationException | IOException | ParseException e) {
			log.error("Error while reading csv file", e);
			throw new PdiImportDataException("Erreur de lecture des campagnes - verifier le fichier.");
		}
	}

	/**
	 * Read the group CSV file and build the object array Field order: Groupe,
	 * NoCampagne, NoGroupe, NoResponsable
	 * 
	 * @param filename
	 * @return a list of groups
	 * @throws PdiImportDataException 
	 */
	public List<Group> readGroups(String filename) throws PdiImportDataException {
		List<Group> groups = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader(filename))) {
			readAndFillGroups(groups, reader);
		} catch (IOException e1) {
			log.error("Could not open file " + filename, e1);
			throw new PdiImportDataException("Could not open group csv file !");
		}
		return groups;
	}

	private void readAndFillGroups(List<Group> groups, CSVReader reader) throws PdiImportDataException {
		String[] lineInArray;
		try {
			double numFaultyLines = 0.0;
			while ((lineInArray = reader.readNext()) != null) {
				try {
					Group group = new Group();
					group.setName(lineInArray[0]);
					group.setCampaignNumber(Long.parseLong(lineInArray[1]));
					group.setNumber(Long.parseLong(lineInArray[2]));
					group.setLeaderNumber(lineInArray[3]);
					groups.add(group);
				} catch (NumberFormatException e1) {
					numFaultyLines += 1.0;
					log.error("Error while reading csv file", e1);
					continue;
				}
			}
			if((numFaultyLines / reader.getLinesRead()) > 0.1) {
				throw new PdiImportDataException("Plus de 10% des données du fichier présentent une erreur de lecture.");
			}
		} catch (CsvValidationException | IOException e) {
			log.error("Error while reading csv file", e);
			throw new PdiImportDataException("Erreur de lecture des groupes - verifier le fichier.");
		}
	}

	/**
	 * Read the seller CSV file and build the object array Field order:
	 * Autorisation, CodeAcheteur, CodeCampagne, MotDePasse, NomVendeur, NoVendeur
	 * 
	 * @param filename
	 * @return a list of sellers
	 * @throws PdiImportDataException 
	 */
	public List<Seller> readSellers(String filename) throws PdiImportDataException {
		List<Seller> sellers = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader(filename))) {
			readAndFillSellers(sellers, reader);
		} catch (IOException e1) {
			log.error("Could not open file " + filename, e1);
			throw new PdiImportDataException("Could not open seller csv file !");
		}
		return sellers;
	}

	private void readAndFillSellers(List<Seller> sellers, CSVReader reader) throws PdiImportDataException {
		String[] lineInArray;
		try {
			double numFaultyLines = 0.0;
			while ((lineInArray = reader.readNext()) != null) {
				try {
					Seller seller = new Seller();
					seller.setAuthorization(lineInArray[0]);
					seller.setBuyerCode(lineInArray[1]);
					seller.setCampaignCode(lineInArray[2]);
					seller.setPassword(lineInArray[3]);
					seller.setName(lineInArray[4]);
					seller.setNumber(Long.parseLong(lineInArray[5]));
					sellers.add(seller);
				} catch (NumberFormatException e1) {
					numFaultyLines += 1.0;
					log.error("Error while reading csv file", e1);
					continue;
				}
			}
			if((numFaultyLines / reader.getLinesRead()) > 0.1) {
				throw new PdiImportDataException("Plus de 10% des données du fichier présentent une erreur de lecture.");
			}
		} catch (CsvValidationException | IOException e) {
			log.error("Error while reading csv file", e);
			throw new PdiImportDataException("Erreur de lecture des vendeurs - verifier le fichier.");
		}
	}

	/**
	 * Read the group link CSV file and build the object array Field order:
	 * NoGroupe, NoVendeur
	 * 
	 * @param groupLinksFilename
	 * @return a list of group links
	 * @throws PdiImportDataException 
	 */
	public List<GroupLink> readGroupLinks(String groupLinksFilename) throws PdiImportDataException {
		List<GroupLink> groupLinks = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new FileReader(groupLinksFilename))) {
			readAndFillGroupLinks(groupLinks, reader);
		} catch (IOException e1) {
			log.error("Could not open file " + groupLinksFilename, e1);
			throw new PdiImportDataException("Could not open groupLink csv file !");
		}
		return groupLinks;
	}

	private void readAndFillGroupLinks(List<GroupLink> groupLinks, CSVReader reader) throws PdiImportDataException {
		String[] lineInArray;
		try {
			double numFaultyLines = 0.0;
			while ((lineInArray = reader.readNext()) != null) {
				try {
					GroupLink groupLink = new GroupLink();
					groupLink.setGroupNumber(Long.parseLong(lineInArray[0]));
					groupLink.setSellerNumber(Long.parseLong(lineInArray[1]));
					String value = sanitize(lineInArray[2]);
					if(value.compareTo("0") == 0 ) {
						groupLink.setGroupForLeaderSales(false);
					} else {
						groupLink.setGroupForLeaderSales(true);
					}
					groupLinks.add(groupLink);
				} catch (NumberFormatException e1) {
					numFaultyLines += 1.0;
					log.error("Error while reading csv file", e1);
					continue;
				}
			}
			if((numFaultyLines / reader.getLinesRead()) > 0.1) {
				throw new PdiImportDataException("Plus de 10% des données du fichier présentent une erreur de lecture.");
			}
		} catch (CsvValidationException | IOException e) {
			log.error("Error while reading csv file", e);
			throw new PdiImportDataException("Erreur de lecture des liens groupes - verifier le fichier.");
		}
	}

	public String getSectionsAndProductsLastImportDate() {
		if (productLastImportRepository.count() > 0) {
			return ImportsUtils.formatInstant(productLastImportRepository.findAll().get(0).getInstant());
		} else {
			return "-";
		}
	}

	public String getGroupsAndSellersLastImportDate() {
		if (groupLastImportRepository.count() > 0) {
			return ImportsUtils.formatInstant(groupLastImportRepository.findAll().get(0).getInstant());
		} else {
			return "-";
		}
	}
}
