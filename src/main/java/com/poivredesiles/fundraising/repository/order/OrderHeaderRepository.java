package com.poivredesiles.fundraising.repository.order;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.poivredesiles.fundraising.model.order.OrderHeader;
import com.poivredesiles.fundraising.model.order.OrderStatusEnum;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Long>, JpaSpecificationExecutor<OrderHeader> {

	Set<OrderHeader> findByOrderStatusAndPdiSeller_id(OrderStatusEnum status, Long id);

	List<OrderHeader> findByOrderStatusOrderByIdDesc(OrderStatusEnum status);

	Optional<OrderHeader> findOneByOrderNumber(Long number);

    List<OrderHeader> findAllByOrderByIdDesc();

	List<OrderHeader> findAllByOrderStatusAndIdIn(OrderStatusEnum orderStatusEnum, List<Long> orderIds);

	// Method to retrieve only the order id, buyerName, and buyerEmail
	Page<OrderHeaderProjection> findAllProjectedBy(Pageable pageable);
}
