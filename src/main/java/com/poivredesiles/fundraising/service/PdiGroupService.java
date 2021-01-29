package com.poivredesiles.fundraising.service;

import java.util.List;

import com.poivredesiles.fundraising.jdbc.dto.Group;

public interface PdiGroupService {

	void importGroups(List<Group> groups);

}
