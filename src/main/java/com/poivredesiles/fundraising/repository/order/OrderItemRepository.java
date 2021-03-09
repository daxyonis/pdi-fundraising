package com.poivredesiles.fundraising.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poivredesiles.fundraising.model.order.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
