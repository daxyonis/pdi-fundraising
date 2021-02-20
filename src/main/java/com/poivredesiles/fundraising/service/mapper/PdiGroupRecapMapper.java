package com.poivredesiles.fundraising.service.mapper;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.poivredesiles.fundraising.model.group.PdiGroup;
import com.poivredesiles.fundraising.service.dto.PdiGroupRecapDTO;

/**
 * Mapper for the entity {@link PdiGroup} and its specialized recap DTO {@link PdiGroupRecapDTO}.
 */
@Mapper(componentModel = "spring", uses = {PdiSellerRecapMapper.class, CurrencyFormattingMapper.class})
public interface PdiGroupRecapMapper {

	@Mapping(target = "groupId", source = "id")
	@Mapping(target = "groupName", source = "name")
	@Mapping(target = "formattedTotalSales", source = "totalSales", qualifiedByName="formatCurrency")
	PdiGroupRecapDTO toDto(PdiGroup pdiGroup);
	
	Set<PdiGroupRecapDTO> toDto(Set<PdiGroup> pdiGroups);    
}
