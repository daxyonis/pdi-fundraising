package com.poivredesiles.fundraising.service;

import java.util.List;

import com.poivredesiles.fundraising.imports.dto.GroupLink;
import com.poivredesiles.fundraising.imports.dto.Seller;
import com.poivredesiles.fundraising.model.group.PdiSeller;

public interface PdiSellerService {

	void importSellers(List<Seller> sellers);		

	void linkSellersToGroup(List<GroupLink> groupLinks);

	PdiSeller save(PdiSeller pdiSeller);

}
