package com.poivredesiles.fundraising.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.poivredesiles.fundraising.model.group.PdiCampaign;
import com.poivredesiles.fundraising.service.dto.PdiCampaignRecapDTO;

/**
 * Mapper for the entity {@link PdiCampaign} and its specialized recap DTO {@link PdiCampaignRecapDTO}.
 */
@Mapper(componentModel = "spring", uses = {PdiGroupRecapMapper.class, CurrencyFormattingMapper.class})
public interface PdiCampaignRecapMapper {

	@Mapping(target = "campaignId", source="id")	
	@Mapping(target = "campaignNumber", source="number")
	@Mapping(target = "leaderName", ignore=true)
	@Mapping(target = "formattedTotalSales", source = "totalSales", qualifiedByName="formatCurrency")
	@Mapping(target = "formattedTotalProfit", source = "totalProfit", qualifiedByName="formatCurrency")	
	@Mapping(target = "pdiGroupRecaps", source = "pdiGroups")
	PdiCampaignRecapDTO toDto(PdiCampaign pdiCampaign);    
        
}
