package com.poivredesiles.fundraising.repository.order;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.model.order.OrderStatusEnum;

public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Long> {

	Set<OrderHeader> findByOrderStatusAndPdiSeller_id(OrderStatusEnum status, Long id);

	Set<OrderHeader> findByOrderStatus(OrderStatusEnum status);

	Optional<OrderHeader> findOneByOrderNumber(Long number);
}
