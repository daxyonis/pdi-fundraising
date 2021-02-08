package com.poivredesiles.fundraising.repository.group;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poivredesiles.fundraising.model.group.PdiCampaign;

public interface PdiCampaignRepository extends JpaRepository<PdiCampaign, Long> {		

	Optional<PdiCampaign> findOneByNumber(Long number);

	int countByBlockedFalse();
}
