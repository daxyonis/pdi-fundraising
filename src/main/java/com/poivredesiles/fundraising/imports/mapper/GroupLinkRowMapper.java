package com.poivredesiles.fundraising.imports.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.poivredesiles.fundraising.imports.dto.GroupLink;

public class GroupLinkRowMapper implements RowMapper<GroupLink> {

	@Override
	public GroupLink mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		GroupLink groupLink = new GroupLink();
		groupLink.setGroupNumber(rs.getLong("NoGroupe"));
		groupLink.setSellerNumber(rs.getLong("NoVendeur"));
//		String value = sanitize(rs.getString("VenteAss"));
//		if(value.compareTo("0") > 0 ) {
//			groupLink.setGroupForLeaderSales(false);
//		} else {
//			groupLink.setGroupForLeaderSales(true);
//		}
		return groupLink;
	}

}
