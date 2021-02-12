package com.poivredesiles.fundraising.service.mapper;

import java.time.LocalDate;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.poivredesiles.fundraising.imports.ImportsUtils;
import com.poivredesiles.fundraising.model.group.PdiSeller;
import com.poivredesiles.fundraising.service.dto.PdiSellerDTO;

/**
 * Mapper for the entity {@link PdiSeller} and its DTO {@link PdiSellerDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, PdiGroupMapper.class})
public interface PdiSellerMapper extends EntityMapper<PdiSellerDTO, PdiSeller> {

    @Mapping(source = "me.id", target = "meId")
    @Mapping(source = "buyer.id", target = "buyerId")
    @Mapping(source = "pdiGroup.id", target = "pdiGroupId")
    @Mapping(source = "pdiGroup.number", target = "pdiGroupNumber")
    @Mapping(source = "pdiGroup.name", target = "pdiGroupName")
    @Mapping(source = "pdiGroup.pdiCampaign.dueDate", target = "formattedPdiCampaignDueDate", qualifiedByName="formatDate")
    @Mapping(source = "pdiGroup.pdiCampaign.organizationName", target = "pdiCampaignOrganization")
    @Mapping(source = "pdiGroup.pdiCampaign.project", target = "pdiCampaignProject")
    PdiSellerDTO toDto(PdiSeller pdiSeller);

    @Mapping(source = "meId", target = "me")
    @Mapping(source = "buyerId", target = "buyer")
    @Mapping(target = "orderHeaders", ignore = true)
    @Mapping(target = "removeOrderHeader", ignore = true)
    @Mapping(source = "pdiGroupId", target = "pdiGroup")
    PdiSeller toEntity(PdiSellerDTO pdiSellerDTO);

    @Named("formatDate")
    public static String formatDate(LocalDate date) {  
    	return ImportsUtils.formatLocalDate(date, "dd/MM/yyyy");
    }
    
    default PdiSeller fromId(Long id) {
        if (id == null) {
            return null;
        }
        PdiSeller pdiSeller = new PdiSeller();
        pdiSeller.setId(id);
        return pdiSeller;
    }
}
