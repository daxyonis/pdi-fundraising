package com.poivredesiles.fundraising.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.model.order.OrderStatusEnum;
import com.poivredesiles.fundraising.service.dto.OrderHeaderCsvDTO;

@Mapper(componentModel = "spring", uses = {MapperUtils.class})
public interface OrderHeaderCsvMapper extends EntityMapper<OrderHeaderCsvDTO, OrderHeader>{

	@Mapping(target="noCommande", source="orderNumber")
	@Mapping(target="noVendeur", source="pdiSeller.number")
	@Mapping(target="nomAcheteur", source="buyerName")
	@Mapping(target="telephone", source="buyerPhone")
	@Mapping(target="remarque", source="buyerNote")
	@Mapping(target="langue", source="buyerLanguage")
	@Mapping(target="etat", source="orderStatus", qualifiedByName="translateStatus")	
	@Mapping(target="noConfirmation", source="confirmationNumber")
	@Mapping(target="dateCommande", source="createdDate", qualifiedByName="instantToString")
	OrderHeaderCsvDTO toDto(OrderHeader orderHeader);
	
	@Named("translateStatus")
    public static String translateStatus(OrderStatusEnum orderStatus) {  
    	switch(orderStatus) {
    	case PENDING: return "EN ATTENTE";
    	case PAID: return "PAYE";
    	case ERROR: return "EN ERREUR";
    	default:
    		throw new IllegalArgumentException("No such status !");
    	}
    }
}
