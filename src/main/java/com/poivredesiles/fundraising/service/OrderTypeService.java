package com.poivredesiles.fundraising.service;

import java.util.List;

import com.poivredesiles.fundraising.jdbc.dto.TypeBC;
import com.poivredesiles.fundraising.service.dto.OrderTypeDTO;

/**
 * Service Interface for managing {@link com.poivredesiles.fundraising.domain.OrderType}.
 */
public interface OrderTypeService {

    /**
     * Save a orderType.
     *
     * @param orderTypeDTO the entity to save.
     * @return the persisted entity.
     */
    OrderTypeDTO save(OrderTypeDTO orderTypeDTO);

    /**
     * Get all the orderTypes.
     *
     * @return the list of entities.
     */
//    List<OrderTypeDTO> findAll();

    /**
     * Get all the orderTypes with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
//    Page<OrderTypeDTO> findAllWithEagerRelationships(Pageable pageable);


    /**
     * Get the "id" orderType.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
//    Optional<OrderTypeDTO> findOne(Long id);

    /**
     * Delete the "id" orderType.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Import a list of TypeBC
     * @param orderTypes
     */
	void importOrderTypes(List<TypeBC> orderTypes);
}
