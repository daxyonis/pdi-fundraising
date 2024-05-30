package com.poivredesiles.fundraising.repository.order;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.model.order.OrderStatusEnum;

public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Long> {

	Set<OrderHeader> findByOrderStatusAndPdiSeller_id(OrderStatusEnum status, Long id);

	List<OrderHeader> findByOrderStatusOrderByIdDesc(OrderStatusEnum status);

	Optional<OrderHeader> findOneByOrderNumber(Long number);

    List<OrderHeader> findAllByOrderByIdDesc();

    List<OrderHeader> findByOrderStatusAndConfirmationDateBetween(OrderStatusEnum orderStatusEnum, Instant instant, Instant instant1);
}
