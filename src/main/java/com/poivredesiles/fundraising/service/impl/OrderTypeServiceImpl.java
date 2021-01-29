package com.poivredesiles.fundraising.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poivredesiles.fundraising.jdbc.dto.TypeBC;
import com.poivredesiles.fundraising.model.order.OrderType;
import com.poivredesiles.fundraising.model.product.PdiProduct;
import com.poivredesiles.fundraising.repository.order.OrderTypeRepository;
import com.poivredesiles.fundraising.repository.product.PdiProductRepository;
import com.poivredesiles.fundraising.service.OrderTypeService;
import com.poivredesiles.fundraising.service.dto.OrderTypeDTO;
import com.poivredesiles.fundraising.service.mapper.OrderTypeMapper;

/**
 * Service Implementation for managing {@link OrderType}.
 */
@Service
@Transactional
public class OrderTypeServiceImpl implements OrderTypeService {

	private final Logger log = LoggerFactory.getLogger(OrderTypeServiceImpl.class);

	private final OrderTypeRepository orderTypeRepository;

	private final OrderTypeMapper orderTypeMapper;
	
	private final PdiProductRepository pdiProductRepository;

	public OrderTypeServiceImpl(OrderTypeRepository orderTypeRepository, OrderTypeMapper orderTypeMapper,
			PdiProductRepository pdiProductRepository) {
		this.orderTypeRepository = orderTypeRepository;
		this.orderTypeMapper = orderTypeMapper;
		this.pdiProductRepository = pdiProductRepository;
	}

	@Override
	public OrderTypeDTO save(OrderTypeDTO orderTypeDTO) {
		log.debug("Request to save OrderType : {}", orderTypeDTO);
		OrderType orderType = orderTypeMapper.toEntity(orderTypeDTO);
		orderType = orderTypeRepository.save(orderType);
		return orderTypeMapper.toDto(orderType);
	}

//    @Override
//    @Transactional(readOnly = true)
//    public List<OrderTypeDTO> findAll() {
//        log.debug("Request to get all OrderTypes");
//        return orderTypeRepository.findAllWithEagerRelationships().stream()
//            .map(orderTypeMapper::toDto)
//            .collect(Collectors.toCollection(LinkedList::new));
//    }
//
//
//    public Page<OrderTypeDTO> findAllWithEagerRelationships(Pageable pageable) {
//        return orderTypeRepository.findAllWithEagerRelationships(pageable).map(orderTypeMapper::toDto);
//    }

//    @Override
//    @Transactional(readOnly = true)
//    public Optional<OrderTypeDTO> findOne(Long id) {
//        log.debug("Request to get OrderType : {}", id);
//        return orderTypeRepository.findOneWithEagerRelationships(id)
//            .map(orderTypeMapper::toDto);
//    }

	@Override
	public void delete(Long id) {
		log.debug("Request to delete OrderType : {}", id);
		orderTypeRepository.deleteById(id);
	}

	@Override
	public void importOrderTypes(List<TypeBC> typeBCList) {	
		if (typeBCList != null) {
			log.info("Importing {} TypeBC", typeBCList.size());
			List<Long> addedTypeBCNumbers = new ArrayList<>();

			for (TypeBC typeBC : typeBCList) {
				// Do not treat same number twice
				if (!addedTypeBCNumbers.contains(typeBC.getNumber())) {

					Optional<OrderType> orderType = orderTypeRepository.findByNumber(typeBC.getNumber());
					List<String> productNumberList = typeBCList.stream()
							.filter(bc -> bc.getNumber() == typeBC.getNumber())
							.map(TypeBC::getProductNumber)
							.collect(Collectors.toList());

					if (orderType.isPresent()) {
						// Update its products
						addProducts(orderType.get(), productNumberList);
					} else {
						// Create new
						OrderType newOrderType = new OrderType();
						newOrderType.setCreatedBy("system");						
						newOrderType.setNumber(typeBC.getNumber());
						orderTypeRepository.save(newOrderType);
						addProducts(newOrderType, productNumberList);
					}
					addedTypeBCNumbers.add(typeBC.getNumber());
				}
			}
		}
	}

	/**
	 * Add products to an order type entity
	 * @param orderType		an order type entity
	 * @param productNumberList	a list of product numbers
	 */
	private void addProducts(OrderType orderType, List<String> productNumberList) {
		// TODO : get the list of products via the product service
		Set<PdiProduct> products = pdiProductRepository.findAllFromProductNumberList(productNumberList);
		orderType.setPdiProducts(products);
		orderType.setLastModifiedBy("system");
		orderTypeRepository.save(orderType);
	}
}
