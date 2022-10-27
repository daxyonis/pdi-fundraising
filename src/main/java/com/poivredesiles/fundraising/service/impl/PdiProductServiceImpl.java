package com.poivredesiles.fundraising.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poivredesiles.fundraising.exception.ResourceNotFoundException;
import com.poivredesiles.fundraising.imports.dto.Product;
import com.poivredesiles.fundraising.model.product.PdiCategory;
import com.poivredesiles.fundraising.model.product.PdiProduct;
import com.poivredesiles.fundraising.repository.product.PdiCategoryRepository;
import com.poivredesiles.fundraising.repository.product.PdiProductRepository;
import com.poivredesiles.fundraising.service.PdiProductService;
import com.poivredesiles.fundraising.service.dto.PdiProductDTO;
import com.poivredesiles.fundraising.service.mapper.PdiProductMapper;

/**
 * Service Implementation for managing {@link PdiProduct}.
 */
@Service
@Transactional
public class PdiProductServiceImpl implements PdiProductService {

    private final Logger log = LoggerFactory.getLogger(PdiProductServiceImpl.class);

    private final PdiProductRepository pdiProductRepository;

    private final PdiProductMapper pdiProductMapper;
    
    private final PdiCategoryRepository pdiCategoryRepository;

    public PdiProductServiceImpl(PdiProductRepository pdiProductRepository,PdiCategoryRepository pdiCategoryRepository, 
    		PdiProductMapper pdiProductMapper) {
        this.pdiProductRepository = pdiProductRepository;
        this.pdiProductMapper = pdiProductMapper;
        this.pdiCategoryRepository = pdiCategoryRepository;
    }

    @Override
    public PdiProductDTO save(PdiProductDTO pdiProductDTO) {
        log.debug("Request to save PdiProduct : {}", pdiProductDTO);
        PdiProduct pdiProduct = pdiProductMapper.toEntity(pdiProductDTO);
        pdiProduct = pdiProductRepository.save(pdiProduct);
        return pdiProductMapper.toDto(pdiProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PdiProductDTO> findAll() {
        log.debug("Request to get all PdiProducts");
        return pdiProductRepository.findAll().stream()
            .map(pdiProductMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<PdiProductDTO> findOne(Long id) {
        log.debug("Request to get PdiProduct : {}", id);
        return pdiProductRepository.findById(id)
            .map(pdiProductMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete PdiProduct : {}", id);
        pdiProductRepository.deleteById(id);
    }

	@Override
	public void importProducts(List<Product> products) {
		if(products != null) {
			log.info("Importing {} products", products.size());
			for(Product product : products) {
				Optional<PdiProduct> pdiProduct = pdiProductRepository.findByProductNumber(product.getNumber());
				if(pdiProduct.isPresent()) {
					// Already exists, just update it
					updateProduct(pdiProduct.get(), product);
				} else {
					// Create new
					PdiProduct newProduct = new PdiProduct();
					newProduct.setCreatedBy("system");
					updateProduct(newProduct, product);
				}
			}
		}
		
	}

	/**
	 * Update one product	
	 * @param pdiProduct	product to update
	 * @param product		product resource with updated values
	 */
	private void updateProduct(PdiProduct pdiProduct, Product product) {
		pdiProduct.setProductNumber(product.getNumber());
		pdiProduct.setLabelNumber(product.getLabelNumber());
		pdiProduct.setNameFr(product.getNameFr());
		pdiProduct.setNameEn(product.getNameEn());
		pdiProduct.setDescriptionFr(product.getDescFr());
		pdiProduct.setDescriptionEn(product.getDescEn());
		pdiProduct.setWeight(product.getWeight());
		pdiProduct.setFormatFr(product.getFormatFr());
		pdiProduct.setFormatEn(product.getFormatEn());

		PdiCategory category = pdiCategoryRepository.findByNumber(product.getSectionNum());
		if(category != null) {
			pdiProduct.setCategory(category);
		} else {
			log.error("Product number {} has unknown category: {}", product.getNumber(), product.getSectionNum());
			throw new ResourceNotFoundException(String.format("Le produit numéro %s a une catégorie inconnue", product.getNumber()));
		}

		pdiProduct.setLastModifiedBy("system");
		pdiProductRepository.save(pdiProduct);
	}
}

