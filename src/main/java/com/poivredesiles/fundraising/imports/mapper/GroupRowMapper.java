package com.poivredesiles.fundraising.imports.mapper;

import static com.poivredesiles.fundraising.imports.ImportsUtils.sanitize;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.poivredesiles.fundraising.imports.dto.Group;

public class GroupRowMapper implements RowMapper<Group> {

	@Override
	public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Group group = new Group();
		group.setNumber(rs.getLong("NoGroupe"));
		group.setName(sanitize(rs.getString("Groupe")));		
		group.setLeaderNumber(sanitize(rs.getString("NoResponsable")));
		group.setCampaignNumber(rs.getLong("NoCampagne"));
		return group;
	}

}
