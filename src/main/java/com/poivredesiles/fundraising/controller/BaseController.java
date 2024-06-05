package com.poivredesiles.fundraising.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.poivredesiles.fundraising.model.user.MyUserDetails;
import com.poivredesiles.fundraising.model.user.RoleEnum;
import com.poivredesiles.fundraising.service.PdiSellerService;
import com.poivredesiles.fundraising.service.dto.PdiSellerDTO;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

@PropertySource("classpath:git.properties")
public class BaseController {

	@Value("${git.commit.id.abbrev}")
	protected String gitCommitId;	
	
	@Value("${git.build.version}")
	protected String buildVersion;
	
	@Autowired
	protected PdiSellerService pdiSellerService;

	@Autowired
	protected LocaleResolver localeResolver;

	protected String language;
	
	@ModelAttribute
	public void populateModel(@AuthenticationPrincipal MyUserDetails userDetails, Model model, HttpServletRequest request) {
		model.addAttribute("gitCommitId", gitCommitId);
		model.addAttribute("buildVersion", buildVersion);
		model.addAttribute("showLangChange", true);
		model.addAttribute("year", LocalDate.now().getYear());
		language = localeResolver.resolveLocale(request).getLanguage();
		model.addAttribute("language", language);
		if(userDetails != null) {
			model.addAttribute("menuShowHome", userDetails.hasAnyAuthority(RoleEnum.ROLE_SELLER, RoleEnum.ROLE_GROUP_LEADER, RoleEnum.ROLE_CAMPAIGN_LEADER, RoleEnum.ROLE_ADMIN));
			model.addAttribute("menuShowSales", userDetails.hasAnyAuthority(RoleEnum.ROLE_GROUP_LEADER, RoleEnum.ROLE_CAMPAIGN_LEADER));
			model.addAttribute("menuShowOrder", userDetails.hasAnyAuthority(RoleEnum.ROLE_BUYER, RoleEnum.ROLE_SELLER, RoleEnum.ROLE_GROUP_LEADER, RoleEnum.ROLE_CAMPAIGN_LEADER));
			model.addAttribute("showCampaignSummaryLink", userDetails.hasAnyAuthority(RoleEnum.ROLE_CAMPAIGN_LEADER));
			if(!userDetails.hasAnyAuthority(RoleEnum.ROLE_ADMIN)) {
				model.addAttribute("seller", getSeller(userDetails));
			}
		}
	}
	
	protected PdiSellerDTO getSeller(MyUserDetails userDetails) {
		return pdiSellerService.getSellerForUser(userDetails);			
	}
}
