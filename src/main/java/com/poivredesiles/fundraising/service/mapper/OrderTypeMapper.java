package com.poivredesiles.fundraising.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.poivredesiles.fundraising.model.order.OrderType;
import com.poivredesiles.fundraising.service.dto.OrderTypeDTO;

/**
 * Mapper for the entity {@link OrderType} and its DTO {@link OrderTypeDTO}.
 */
@Mapper(componentModel = "spring", uses = {PdiProductMapper.class})
public interface OrderTypeMapper extends EntityMapper<OrderTypeDTO, OrderType> {


    @Mapping(target = "pdiProducts", ignore = true)    
    OrderType toEntity(OrderTypeDTO orderTypeDTO);

    default OrderType fromId(Long id) {
        if (id == null) {
            return null;
        }
        OrderType orderType = new OrderType();
        orderType.setId(id);
        return orderType;
    }
}
