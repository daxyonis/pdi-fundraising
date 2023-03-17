package com.poivredesiles.fundraising.controller;

import com.poivredesiles.fundraising.imports.CsvImportService;
import com.poivredesiles.fundraising.service.OrderService;
import com.poivredesiles.fundraising.service.PdiCampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController extends BaseController {

    private final Logger log = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private CsvImportService csvImportService;

    @Autowired
    private PdiCampaignService pdiCampaignService;

    @GetMapping("/admin")
    @Secured("ROLE_ADMIN")
    public String admin(Model model) {
        log.info("Requested Admin Page");
        model.addAttribute("sectionsAndProductsLastImport", csvImportService.getSectionsAndProductsLastImportDate());
        model.addAttribute("groupsAndSellersLastImport", csvImportService.getGroupsAndSellersLastImportDate());
        model.addAttribute("showWarning", pdiCampaignService.thereAreActiveCampaigns());
        return "views/admin";
    }
}
