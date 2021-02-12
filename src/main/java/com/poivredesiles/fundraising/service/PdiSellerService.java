package com.poivredesiles.fundraising.service;

import java.util.List;

import com.poivredesiles.fundraising.imports.dto.GroupLink;
import com.poivredesiles.fundraising.imports.dto.Seller;
import com.poivredesiles.fundraising.model.group.PdiSeller;
import com.poivredesiles.fundraising.model.user.MyUserDetails;
import com.poivredesiles.fundraising.service.dto.PdiProductDTO;

public interface PdiSellerService {

	void importSellers(List<Seller> sellers);		

	void linkSellersToGroup(List<GroupLink> groupLinks);

	PdiSeller save(PdiSeller pdiSeller);

	/**
	 * Get the list of products available to order for the given user
	 * @param userDetails user info
	 * @return a list of products available
	 */
	List<PdiProductDTO> getProductsForUser(MyUserDetails userDetails);

}
