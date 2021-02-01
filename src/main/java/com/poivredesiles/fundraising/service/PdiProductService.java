package com.poivredesiles.fundraising.service;

import java.util.List;
import java.util.Optional;

import com.poivredesiles.fundraising.imports.dto.Product;
import com.poivredesiles.fundraising.service.dto.PdiProductDTO;

/**
 * Service Interface for managing {@link com.poivredesiles.fundraising.model.product.domain.PdiProduct}.
 */
public interface PdiProductService {

    /**
     * Save a pdiProduct.
     *
     * @param pdiProductDTO the entity to save.
     * @return the persisted entity.
     */
    PdiProductDTO save(PdiProductDTO pdiProductDTO);

    /**
     * Get all the pdiProducts.
     *
     * @return the list of entities.
     */
    List<PdiProductDTO> findAll();


    /**
     * Get the "id" pdiProduct.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PdiProductDTO> findOne(Long id);

    /**
     * Delete the "id" pdiProduct.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Import a new/updated list of products 
     * @param products
     */
	void importProducts(List<Product> products);
}
