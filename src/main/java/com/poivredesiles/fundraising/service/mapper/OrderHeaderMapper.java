package com.poivredesiles.fundraising.service.mapper;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.poivredesiles.fundraising.imports.ImportsUtils;
import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.service.dto.OrderHeaderDTO;

/**
 * Mapper for the entity {@link OrderHeader} and its DTO {@link OrderHeaderDTO}.
 */
@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, CurrencyFormattingMapper.class, MapperUtils.class})
public interface OrderHeaderMapper extends EntityMapper<OrderHeaderDTO, OrderHeader> {	
	
	@Mapping(source="total", target="formattedTotal", qualifiedByName="formatCurrency")
    @Mapping(source="confirmationDate", target="formattedConfirmationDate", qualifiedByName = "instantToString")
    OrderHeaderDTO toDto(OrderHeader orderHeader);

    @Mapping(target = "pdiSeller", ignore = true)
    @Mapping(target = "orderItems", ignore = true)    
    OrderHeader toEntity(OrderHeaderDTO orderHeaderDTO);
    
    default OrderHeader fromId(Long id) {
        if (id == null) {
            return null;
        }
        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setId(id);
        return orderHeader;
    }
}
