package com.poivredesiles.fundraising.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@PropertySource("classpath:git.properties")
public class MainController {
	
	private final Logger log = LoggerFactory.getLogger(MainController.class);

	@Value("${git.commit.id.abbrev}")
	private String gitCommitId;	
	
	@Value("${git.build.version}")
	private String buildVersion; 
	
	@ModelAttribute
	public void populateModel(Model model) {
		model.addAttribute("gitCommitId", gitCommitId);
		model.addAttribute("buildVersion", buildVersion);
	}
	
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
	public String admin() {
		log.info("Requested Admin Page");
		return "views/admin";
	}
}
