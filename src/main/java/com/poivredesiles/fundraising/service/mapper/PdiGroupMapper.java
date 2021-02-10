package com.poivredesiles.fundraising.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.poivredesiles.fundraising.model.group.PdiGroup;
import com.poivredesiles.fundraising.service.dto.PdiGroupDTO;

/**
 * Mapper for the entity {@link PdiGroup} and its DTO {@link PdiGroupDTO}.
 */
@Mapper(componentModel = "spring", uses = {PdiCampaignMapper.class})
public interface PdiGroupMapper extends EntityMapper<PdiGroupDTO, PdiGroup> {

    @Mapping(source = "pdiCampaign.id", target = "pdiCampaignId")
    PdiGroupDTO toDto(PdiGroup pdiGroup);

    @Mapping(target = "pdiSellers", ignore = true)
    @Mapping(target = "removePdiSeller", ignore = true)
    @Mapping(source = "pdiCampaignId", target = "pdiCampaign")
    PdiGroup toEntity(PdiGroupDTO pdiGroupDTO);

    default PdiGroup fromId(Long id) {
        if (id == null) {
            return null;
        }
        PdiGroup pdiGroup = new PdiGroup();
        pdiGroup.setId(id);
        return pdiGroup;
    }
}
