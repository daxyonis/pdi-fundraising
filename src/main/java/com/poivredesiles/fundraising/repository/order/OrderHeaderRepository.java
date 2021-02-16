package com.poivredesiles.fundraising.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poivredesiles.fundraising.model.order.OrderHeader;

public interface OrderHeaderRepository extends JpaRepository<OrderHeader, Long> {

}
