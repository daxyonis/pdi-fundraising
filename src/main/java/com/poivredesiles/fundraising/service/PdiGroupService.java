package com.poivredesiles.fundraising.service;

import java.util.List;

import com.poivredesiles.fundraising.imports.dto.Group;
import com.poivredesiles.fundraising.model.group.PdiGroup;
import com.poivredesiles.fundraising.model.user.MyUserDetails;
import com.poivredesiles.fundraising.resource.MultiGroupRecap;
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
	 * Permission method : does the current User have a permission to access this group ?
	 * If he/she a seller in this group or if he/she is a leader of this group , then Yes, returns true; else returns false.
	 * @param currentUser 
	 * @param groupId
	 * @return
	 */
	boolean hasAccess(MyUserDetails currentUser, Long groupId);

	/**
	 * Get the recap of groups that are under the responsibility of a given group leader
	 * @param userId Group Leader User Id
	 * @return
	 */
	MultiGroupRecap getMultiGroupRecapForLeader(Long userId);

	/**
	 * Delete one group
	 * @param pdiGroup
	 */
	void deletePdiGroup(PdiGroup pdiGroup);
}
