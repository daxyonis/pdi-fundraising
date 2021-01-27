package com.poivredesiles.fundraising.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poivredesiles.fundraising.jdbc.dto.Section;
import com.poivredesiles.fundraising.model.product.PdiCategory;
import com.poivredesiles.fundraising.repository.product.PdiCategoryRepository;
import com.poivredesiles.fundraising.service.PdiCategoryService;
import com.poivredesiles.fundraising.service.dto.PdiCategoryDTO;
import com.poivredesiles.fundraising.service.mapper.PdiCategoryMapper;

/**
 * Service Implementation for managing {@link PdiCategory}.
 */
@Service
@Transactional
public class PdiCategoryServiceImpl implements PdiCategoryService {

    private final Logger log = LoggerFactory.getLogger(PdiCategoryServiceImpl.class);

    private final PdiCategoryRepository pdiCategoryRepository;

    private final PdiCategoryMapper pdiCategoryMapper;

    public PdiCategoryServiceImpl(PdiCategoryRepository pdiCategoryRepository, PdiCategoryMapper pdiCategoryMapper) {
        this.pdiCategoryRepository = pdiCategoryRepository;
        this.pdiCategoryMapper = pdiCategoryMapper;
    }

    @Override
    public PdiCategoryDTO save(PdiCategoryDTO pdiCategoryDTO) {
        log.debug("Request to save PdiCategory : {}", pdiCategoryDTO);
        PdiCategory pdiCategory = pdiCategoryMapper.toEntity(pdiCategoryDTO);
        pdiCategory = pdiCategoryRepository.save(pdiCategory);
        return pdiCategoryMapper.toDto(pdiCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PdiCategoryDTO> findAll() {
        log.debug("Request to get all PdiCategories");
        return pdiCategoryRepository.findAll().stream()
            .map(pdiCategoryMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<PdiCategoryDTO> findOne(Long id) {
        log.debug("Request to get PdiCategory : {}", id);
        return pdiCategoryRepository.findById(id)
            .map(pdiCategoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete PdiCategory : {}", id);
        pdiCategoryRepository.deleteById(id);
    }

	@Override
	public void importSections(List<Section> sections) {		
		if(sections != null) {
			log.info("Importing {} sections", sections.size());
			for(Section section : sections) {
				PdiCategory category = pdiCategoryRepository.findByNumber(section.getNumber());
				if(category == null) {					
					category = new PdiCategory();					
				}
				updateCategory(category, section);
			}
		}		
	}
	
	private void updateCategory(PdiCategory category, Section section) {
		category.setNumber(section.getNumber());
		category.setDescriptionFr(section.getSectionFr());
		category.setDescriptionEn(section.getSectionEn());
		category.setUnitPrice(section.getUnitPrice());
		pdiCategoryRepository.save(category);
	}
}
