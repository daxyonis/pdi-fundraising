package com.poivredesiles.fundraising.service;

import java.util.List;

import com.poivredesiles.fundraising.imports.dto.GroupLink;
import com.poivredesiles.fundraising.imports.dto.Seller;

public interface PdiSellerService {

	void importSellers(List<Seller> sellers, List<GroupLink> groupLinks);

}
