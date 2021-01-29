package com.poivredesiles.fundraising.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.poivredesiles.fundraising.model.product.PdiCategory;
import com.poivredesiles.fundraising.service.dto.PdiCategoryDTO;

/**
 * Mapper for the entity {@link PdiCategory} and its DTO {@link PdiCategoryDTO}.
 */
@Mapper(componentModel = "spring", uses = {PdiProductMapper.class})
public interface PdiCategoryMapper extends EntityMapper<PdiCategoryDTO, PdiCategory> {

    @Mapping(target = "pdiProducts", ignore = true)
    PdiCategory toEntity(PdiCategoryDTO pdiCategoryDTO);

    default PdiCategory fromId(Long id) {
        if (id == null) {
            return null;
        }
        PdiCategory pdiCategory = new PdiCategory();
        pdiCategory.setId(id);
        return pdiCategory;
    }
}
