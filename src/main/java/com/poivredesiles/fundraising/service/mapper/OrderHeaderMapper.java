package com.poivredesiles.fundraising.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.service.dto.OrderHeaderDTO;

/**
 * Mapper for the entity {@link OrderHeader} and its DTO {@link OrderHeaderDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface OrderHeaderMapper extends EntityMapper<OrderHeaderDTO, OrderHeader> {

    OrderHeaderDTO toDto(OrderHeader orderHeader);

    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "removeOrderItem", ignore = true)    
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
