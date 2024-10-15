package com.poivredesiles.fundraising.repository.group;

import com.poivredesiles.fundraising.model.group.PdiCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PdiCampaignRepository extends JpaRepository<PdiCampaign, Long> {

	// Custom query to return campaigns that match the conditions
	@Query("SELECT c FROM PdiCampaign c JOIN c.notifications n WHERE c.dueDate <= :now AND c.closed = true and n.dateSent is null")
	List<PdiCampaign> findClosedWithNotificationsByDueDate(LocalDate now);

	Optional<PdiCampaign> findOneByNumber(Long number);

	int countByBlockedFalse();	

	List<PdiCampaign> findAllByClosedAndBlocked(boolean b, boolean blocked);

	List<PdiCampaign> findAllByClosed(boolean b);

	Optional<PdiCampaign> findByLeaderNum(String number);
	
	List<PdiCampaign> findByClosedTrueAndBlockedTrueAndExportDateNotNullAndBlockedDateLessThan(LocalDate date);

    List<PdiCampaign> findByClosedTrueAndIdIn(List<Long> campaignIds);

    List<PdiCampaign> findByDueDateAndClosedFalse(LocalDate now);


}
