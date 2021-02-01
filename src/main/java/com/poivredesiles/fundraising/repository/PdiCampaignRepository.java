package com.poivredesiles.fundraising.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poivredesiles.fundraising.model.PdiCampaign;

public interface PdiCampaignRepository extends JpaRepository<PdiCampaign, Long> {		

	Optional<PdiCampaign> findOneByNumber(String number);
}
