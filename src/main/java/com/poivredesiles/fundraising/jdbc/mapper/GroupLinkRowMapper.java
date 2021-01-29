package com.poivredesiles.fundraising.jdbc.mapper;

import static com.poivredesiles.fundraising.jdbc.JdbcUtils.sanitize;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.poivredesiles.fundraising.jdbc.dto.GroupLink;

public class GroupLinkRowMapper implements RowMapper<GroupLink> {

	@Override
	public GroupLink mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		GroupLink groupLink = new GroupLink();
		groupLink.setGroupNumber(rs.getLong("NoGroupe"));
		groupLink.setSellerNumber(rs.getLong("NoVendeur"));
		groupLink.setSaleThing(sanitize(rs.getString("VenteAss")));
		return groupLink;
	}

}
