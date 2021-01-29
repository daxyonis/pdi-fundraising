package com.poivredesiles.fundraising.jdbc.mapper;

import static com.poivredesiles.fundraising.jdbc.JdbcUtils.sanitize;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.poivredesiles.fundraising.jdbc.dto.Group;

public class GroupRowMapper implements RowMapper<Group> {

	@Override
	public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Group group = new Group();
		group.setNumber(rs.getLong("NoGroupe"));
		group.setName(sanitize(rs.getString("Groupe")));
		group.setOrganizationNumber(sanitize(rs.getString("NoOrganisme")));
		group.setLeaderNumber(sanitize(rs.getString("NoResponsable")));
		return group;
	}

}
