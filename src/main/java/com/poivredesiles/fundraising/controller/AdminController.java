package com.poivredesiles.fundraising.controller;

import com.poivredesiles.fundraising.imports.CsvImportService;
import com.poivredesiles.fundraising.service.DateUtils;
import com.poivredesiles.fundraising.service.OrderService;
import com.poivredesiles.fundraising.service.PdiCampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class AdminController extends BaseController {

    private final Logger log = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private CsvImportService csvImportService;

    @Autowired
    private PdiCampaignService pdiCampaignService;

    @Autowired
    private DateUtils dateUtils;

    @GetMapping("/admin")
    @Secured("ROLE_ADMIN")
    public String admin(Model model) {
        log.info("Requested Admin Page");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        model.addAttribute("sectionsAndProductsLastImport", csvImportService.getSectionsAndProductsLastImportDate());
        stopWatch.stop();
        log.info("First request took " + stopWatch.getTotalTimeMillis() + " ms");
        stopWatch.start();
        model.addAttribute("showWarning", pdiCampaignService.thereAreActiveCampaigns());
        stopWatch.stop();
        log.info("Second request took " + stopWatch.getTotalTimeMillis() + " ms");
        stopWatch.start();
        model.addAttribute("today", dateUtils.today());
        model.addAttribute("dateFormat", dateUtils.getDateFormat().toLowerCase());
        stopWatch.stop();
        log.info("Third and fourth requests took " + stopWatch.getTotalTimeMillis() + " ms");
        return "views/admin";
    }
}
