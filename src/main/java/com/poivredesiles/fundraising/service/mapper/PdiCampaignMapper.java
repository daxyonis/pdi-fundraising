package com.poivredesiles.fundraising.service.mapper;

import java.time.LocalDate;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.poivredesiles.fundraising.imports.ImportsUtils;
import com.poivredesiles.fundraising.model.group.PdiCampaign;
import com.poivredesiles.fundraising.service.dto.PdiCampaignDTO;

/**
 * Mapper for the entity {@link PdiCampaign} and its DTO {@link PdiCampaignDTO}.
 */
@Mapper(componentModel = "spring", uses = {OrderTypeMapper.class})
public interface PdiCampaignMapper extends EntityMapper<PdiCampaignDTO, PdiCampaign> {

    @Mapping(source = "orderType.id", target = "orderTypeId")
    @Mapping(source = "dueDate", target = "formattedDueDate", qualifiedByName="formatDate")
    @Mapping(source = "closedDate", target = "formattedClosedDate", qualifiedByName="formatDate")
    @Mapping(source = "blockedDate", target = "formattedBlockedDate", qualifiedByName="formatDate")
    @Mapping(source = "exportDate", target = "formattedExportDate", qualifiedByName="formatDate")
    PdiCampaignDTO toDto(PdiCampaign pdiCampaign);

    @Mapping(target = "pdiGroups", ignore = true)    
    @Mapping(source = "orderTypeId", target = "orderType")
    PdiCampaign toEntity(PdiCampaignDTO pdiCampaignDTO);

    @Named("formatDate")
    public static String formatDate(LocalDate date) {  
    	return ImportsUtils.formatLocalDate(date, "dd/MM/yyyy");
    }
    
    default PdiCampaign fromId(Long id) {
        if (id == null) {
            return null;
        }
        PdiCampaign pdiCampaign = new PdiCampaign();
        pdiCampaign.setId(id);
        return pdiCampaign;
    }
}
