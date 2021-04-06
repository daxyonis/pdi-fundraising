package com.poivredesiles.fundraising.service;

import java.util.List;

import com.poivredesiles.fundraising.exception.InvalidUsernameException;
import com.poivredesiles.fundraising.imports.dto.GroupLink;
import com.poivredesiles.fundraising.imports.dto.Seller;
import com.poivredesiles.fundraising.model.group.PdiSeller;
import com.poivredesiles.fundraising.model.user.MyUserDetails;
import com.poivredesiles.fundraising.service.dto.PdiProductDTO;
import com.poivredesiles.fundraising.service.dto.PdiSellerDTO;

public interface PdiSellerService {

	void importSellers(List<Seller> sellers) throws InvalidUsernameException;		

	void linkSellersToGroup(List<GroupLink> groupLinks);

	PdiSeller save(PdiSeller pdiSeller);

	/**
	 * Get the list of products available to order for the current user
	 * @param userDetails current user info
	 * @return a list of products available
	 */
	List<PdiProductDTO> getProductsForUser(MyUserDetails userDetails);

	/**
	 * Get the seller for the current user : the user himself can be a seller,
	 * or the user can be a buyer associated to a seller
	 * @param userDetails current user info
	 * @return the seller info corresponding to the current user
	 */
	PdiSellerDTO getSellerForUser(MyUserDetails userDetails);

	/**
	 * Delete the seller : also deletes the buyer user and the 'me' user (i.e. the user that represents the seller)
	 * if his role is not ADMIN
	 * @param pdiSeller
	 */
	void deletePdiSeller(PdiSeller pdiSeller);	

}
