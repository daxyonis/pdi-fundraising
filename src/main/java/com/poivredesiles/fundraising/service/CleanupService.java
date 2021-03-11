package com.poivredesiles.fundraising.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * This service is for cleaning up (erasing) campaign data that
 * has been blocked more than one year ago.
 * @author evita
 *
 */
@Service
public class CleanupService {

	@Autowired
	private PdiCampaignService pdiCampaignService;
	
	private Logger log = LoggerFactory.getLogger(CleanupService.class);
	
	// Runs everyday at 3:00am
	// cron = ("sec min hour dayOfMonth month dayOfWeek")	
//	@Scheduled(cron="0 * * * * *")		// each minute
	@Scheduled(cron="0 0 3 * * *")
	public void cleanup() {
		log.info("****************** RUNNING CLEANUP *******************");
		pdiCampaignService.deleteBlockedFor1Year();		
	}
}
