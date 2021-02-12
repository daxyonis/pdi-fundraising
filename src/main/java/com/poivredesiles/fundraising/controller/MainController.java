package com.poivredesiles.fundraising.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.poivredesiles.fundraising.imports.CsvImportService;
import com.poivredesiles.fundraising.service.PdiCampaignService;

@Controller
public class MainController extends BaseController {
	
	private final Logger log = LoggerFactory.getLogger(MainController.class);	
	
	@Autowired
	private CsvImportService csvImportService;
	
	@Autowired
	private PdiCampaignService pdiCampaignService;
	
	@GetMapping("/")
	public String home() {
		log.info("Requested Home Page");
		return "index";
	}
	
	@GetMapping("/login")
	public String login() {
		log.info("Requested Login Page");
		return "views/login";
	}
	
	@GetMapping("/admin")
	public String admin(Model model) {
		log.info("Requested Admin Page");
		model.addAttribute("homeUrl", "/admin");
		model.addAttribute("menuShowHome", true);
		model.addAttribute("menuShowSales", false);
		model.addAttribute("menuShowOrder", false);
		model.addAttribute("sectionsAndProductsLastImport", csvImportService.getSectionsAndProductsLastImportDate());
		model.addAttribute("groupsAndSellersLastImport", csvImportService.getGroupsAndSellersLastImportDate());
		model.addAttribute("showWarning", pdiCampaignService.thereAreActiveCampaigns());
		return "views/admin";
	}
}
