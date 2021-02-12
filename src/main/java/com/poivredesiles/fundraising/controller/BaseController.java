package com.poivredesiles.fundraising.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

@PropertySource("classpath:git.properties")
public class BaseController {

	@Value("${git.commit.id.abbrev}")
	protected String gitCommitId;	
	
	@Value("${git.build.version}")
	protected String buildVersion;
	
	
	@ModelAttribute
	public void populateModel(Model model) {
		model.addAttribute("gitCommitId", gitCommitId);
		model.addAttribute("buildVersion", buildVersion);
	}
	
}
