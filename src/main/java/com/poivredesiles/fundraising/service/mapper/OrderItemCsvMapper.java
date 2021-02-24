package com.poivredesiles.fundraising.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.poivredesiles.fundraising.model.order.OrderItem;
import com.poivredesiles.fundraising.service.dto.OrderItemCsvDTO;

@Mapper(componentModel = "spring", uses = {})
public interface OrderItemCsvMapper extends EntityMapper<OrderItemCsvDTO, OrderItem> {

	@Mapping(target="noCommande", source="orderNumber")
	@Mapping(target="noProduit", source="productNumber")
	@Mapping(target="quantite", source="quantity")
	@Mapping(target="prixUnitaire", source="unitPrice")
	@Mapping(target="sousTotal", source="subTotal")
	OrderItemCsvDTO toDto(OrderItem orderItem);
}
