package com.poivredesiles.fundraising.service.mapper;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
	@Mapping(target="formattedTotalSales", source="ordersTotal", qualifiedByName="formatCurrency")
	PdiSellerRecapDTO toDto(PdiSeller seller);
	
	Set<PdiSellerRecapDTO> toDto(Set<PdiSeller> sellers);	
}
