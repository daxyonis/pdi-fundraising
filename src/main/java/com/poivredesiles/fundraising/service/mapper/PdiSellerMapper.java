package com.poivredesiles.fundraising.service.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
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
    @Mapping(source = "pdiGroup.pdiCampaign.dueDate", target = "pdiCampaignDueDate", qualifiedByName = "localDateToDate")
    @Mapping(source = "pdiGroup.pdiCampaign.organizationName", target = "pdiCampaignOrganization")
    @Mapping(source = "pdiGroup.pdiCampaign.number", target = "pdiCampaignNumber")
    @Mapping(source = "pdiGroup.pdiCampaign.id", target = "pdiCampaignId")
    @Mapping(source = "pdiGroup.pdiCampaign.project", target = "pdiCampaignProject")
    @Mapping(source = "pdiGroup.pdiCampaign.closed", target = "pdiCampaignClosed")
    @Mapping(source="ordersTotal", target="formattedOrdersTotal", qualifiedByName="formatCurrency")    
    PdiSellerDTO toDto(PdiSeller pdiSeller);

    @Mapping(source = "meId", target = "me")
    @Mapping(source = "buyerId", target = "buyer")
    @Mapping(target = "orderHeaders", ignore = true)
    @Mapping(target = "removeOrderHeader", ignore = true)
    @Mapping(source = "pdiGroupId", target = "pdiGroup")
    PdiSeller toEntity(PdiSellerDTO pdiSellerDTO);

    @Named("localDateToDate")
    static Date localDateToDate(LocalDate localDate) {
    	return ImportsUtils.convertToDate(localDate);
    }
    
    @Named("formatCurrency")
    static String formatCurrency(BigDecimal amount) {
    	return ImportsUtils.formatCurrency(amount);
    }
    
    @AfterMapping
    static void cleanGroupName(@MappingTarget PdiSellerDTO pdiSellerDTO) {
    	String groupName = pdiSellerDTO.getPdiGroupName() == null ? "" : pdiSellerDTO.getPdiGroupName(); 
		if(groupName.strip().compareTo("--") == 0) {
			groupName = "";
		}
		pdiSellerDTO.setPdiGroupName(groupName);
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
