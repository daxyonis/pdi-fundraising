package com.poivredesiles.fundraising.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.poivredesiles.fundraising.model.group.PdiCampaign;
import com.poivredesiles.fundraising.service.dto.PdiCampaignDTO;

/**
 * Mapper for the entity {@link PdiCampaign} and its DTO {@link PdiCampaignDTO}.
 */
@Mapper(componentModel = "spring", uses = {OrderTypeMapper.class})
public interface PdiCampaignMapper extends EntityMapper<PdiCampaignDTO, PdiCampaign> {

    @Mapping(source = "orderType.id", target = "orderTypeId")
    PdiCampaignDTO toDto(PdiCampaign pdiCampaign);

    @Mapping(target = "pdiGroups", ignore = true)    
    @Mapping(source = "orderTypeId", target = "orderType")
    PdiCampaign toEntity(PdiCampaignDTO pdiCampaignDTO);

    default PdiCampaign fromId(Long id) {
        if (id == null) {
            return null;
        }
        PdiCampaign pdiCampaign = new PdiCampaign();
        pdiCampaign.setId(id);
        return pdiCampaign;
    }
}
