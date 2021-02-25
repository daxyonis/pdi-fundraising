package com.poivredesiles.fundraising.service;

import java.util.List;

import com.poivredesiles.fundraising.imports.dto.Group;
import com.poivredesiles.fundraising.model.user.MyUserDetails;
import com.poivredesiles.fundraising.service.dto.PdiGroupRecapDTO;

public interface PdiGroupService {

	void importGroups(List<Group> groups);

	/**
	 * Gather the group recap information for a given group
	 * @param groupId the group id
	 * @return the group recap DTO
	 */
	PdiGroupRecapDTO getGroupRecap(Long groupId);
	
	/**
	 * Permission method : is the current User a member of this group,
	 * i.e. is he/she a seller in this group ? If so, returns true; else returns false.
	 * @param currentUser 
	 * @param groupId
	 * @return
	 */
	boolean hasAccess(MyUserDetails currentUser, Long groupId);
}
