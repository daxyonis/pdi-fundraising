package com.poivredesiles.fundraising.service.mapper;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.poivredesiles.fundraising.model.group.PdiGroup;
import com.poivredesiles.fundraising.model.group.PdiSeller;
import com.poivredesiles.fundraising.service.dto.PdiGroupRecapDTO;
import com.poivredesiles.fundraising.service.dto.PdiSellerRecapDTO;

/**
 * Mapper for the entity {@link PdiGroup} and its specialized recap DTO {@link PdiGroupRecapDTO}.
 */
@Mapper(componentModel = "spring", uses = {CurrencyFormattingMapper.class})
public interface PdiSellerRecapMapper {

	@Mapping(target="sellerName", source="name")
	@Mapping(target="numPaidOrders", source="numOrders")
	@Mapping(target="formattedTotalSales", source="ordersTotal", qualifiedByName="formatCurrency")	
	PdiSellerRecapDTO toDto(PdiSeller seller);
	
	List<PdiSellerRecapDTO> toDto(Set<PdiSeller> sellers);	
	
	@AfterMapping
	public static void sortGroups(@MappingTarget List<PdiSellerRecapDTO> pdiSellerRecapDTOs) {
		pdiSellerRecapDTOs.sort(Comparator.comparing(PdiSellerRecapDTO::getSellerName)); 
	}
}
