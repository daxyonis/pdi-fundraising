package com.poivredesiles.fundraising.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.poivredesiles.fundraising.model.order.OrderItem;
import com.poivredesiles.fundraising.service.dto.OrderItemDTO;

/**
 * Mapper for the entity {@link OrderItem} and its DTO {@link OrderItemDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface OrderItemMapper extends EntityMapper<OrderItemDTO, OrderItem> {

    OrderItemDTO toDto(OrderItem orderItem);

    @Mapping(target = "product", ignore=true)
    @Mapping(target = "header", ignore=true)
    OrderItem toEntity(OrderItemDTO orderItemDTO);

    default OrderItem fromId(Long id) {
        if (id == null) {
            return null;
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setId(id);
        return orderItem;
    }
}
