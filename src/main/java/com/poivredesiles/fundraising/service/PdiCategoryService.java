package com.poivredesiles.fundraising.service;

import java.util.List;
import java.util.Optional;

import com.poivredesiles.fundraising.imports.dto.Section;
import com.poivredesiles.fundraising.service.dto.PdiCategoryDTO;

/**
 * Service Interface for managing {@link com.poivredesiles.fundraising.model.product.domain.PdiCategory}.
 */
public interface PdiCategoryService {

    /**
     * Save a pdiCategory.
     *
     * @param pdiCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    PdiCategoryDTO save(PdiCategoryDTO pdiCategoryDTO);

    /**
     * Get all the pdiCategories.
     *
     * @return the list of entities.
     */
    List<PdiCategoryDTO> findAll();


    /**
     * Get the "id" pdiCategory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PdiCategoryDTO> findOne(Long id);

    /**
     * Delete the "id" pdiCategory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Import new/updated sections
     * @param sections
     */
	void importSections(List<Section> sections);
}
