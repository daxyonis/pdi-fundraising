package com.poivredesiles.fundraising.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
    PdiSellerDTO toDto(PdiSeller pdiSeller);

    @Mapping(source = "meId", target = "me")
    @Mapping(source = "buyerId", target = "buyer")
    @Mapping(target = "orderHeaders", ignore = true)
    @Mapping(target = "removeOrderHeader", ignore = true)
    @Mapping(source = "pdiGroupId", target = "pdiGroup")
    PdiSeller toEntity(PdiSellerDTO pdiSellerDTO);

    default PdiSeller fromId(Long id) {
        if (id == null) {
            return null;
        }
        PdiSeller pdiSeller = new PdiSeller();
        pdiSeller.setId(id);
        return pdiSeller;
    }
}
