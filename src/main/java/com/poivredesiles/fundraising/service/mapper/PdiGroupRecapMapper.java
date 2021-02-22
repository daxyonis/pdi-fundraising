package com.poivredesiles.fundraising.service.mapper;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

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
	@Mapping(target = "pdiSellerRecaps", source = "pdiSellers")
	PdiGroupRecapDTO toDto(PdiGroup pdiGroup);
	
	List<PdiGroupRecapDTO> toDto(Set<PdiGroup> pdiGroups);    
	
	@AfterMapping
	public static void sortGroups(@MappingTarget List<PdiGroupRecapDTO> pdiGroupRecapDTOs) {
		pdiGroupRecapDTOs.sort(Comparator.comparing(PdiGroupRecapDTO::getGroupName)); 
	}
}
