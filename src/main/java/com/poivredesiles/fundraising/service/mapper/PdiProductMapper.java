package com.poivredesiles.fundraising.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.poivredesiles.fundraising.model.PdiProduct;
import com.poivredesiles.fundraising.service.dto.PdiProductDTO;

/**
 * Mapper for the entity {@link PdiProduct} and its DTO {@link PdiProductDTO}.
 */
@Mapper(componentModel = "spring", uses = {PdiCategoryMapper.class})
public interface PdiProductMapper extends EntityMapper<PdiProductDTO, PdiProduct> {

    @Mapping(source = "category.id", target = "categoryId")
    PdiProductDTO toDto(PdiProduct pdiProduct);

    @Mapping(source = "categoryId", target = "category")
    PdiProduct toEntity(PdiProductDTO pdiProductDTO);

    default PdiProduct fromId(Long id) {
        if (id == null) {
            return null;
        }
        PdiProduct pdiProduct = new PdiProduct();
        pdiProduct.setId(id);
        return pdiProduct;
    }
}
