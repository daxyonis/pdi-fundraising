package com.poivredesiles.fundraising.imports.mapper;

import static com.poivredesiles.fundraising.imports.ImportsUtils.sanitize;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.poivredesiles.fundraising.imports.dto.TypeBC;

public class TypeBCRowMapper implements RowMapper<TypeBC> {

	@Override
	public TypeBC mapRow(ResultSet rs, int rowNum) throws SQLException {
		TypeBC orderType = new TypeBC();
		orderType.setProductNumber(sanitize(rs.getString("NoProduit")));
		orderType.setNumber(rs.getLong("NoTypeBC"));
		return orderType;
	}

}
