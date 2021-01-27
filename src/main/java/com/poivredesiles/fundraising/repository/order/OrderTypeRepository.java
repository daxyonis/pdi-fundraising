package com.poivredesiles.fundraising.repository.order;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poivredesiles.fundraising.model.order.OrderType;

public interface OrderTypeRepository extends JpaRepository<OrderType, Long> {

	Optional<OrderType> findByNumber(Long number);

}
