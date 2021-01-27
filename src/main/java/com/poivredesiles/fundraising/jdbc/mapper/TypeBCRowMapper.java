package com.poivredesiles.fundraising.jdbc.mapper;

import static com.poivredesiles.fundraising.jdbc.JdbcUtils.sanitize;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.poivredesiles.fundraising.jdbc.dto.TypeBC;

public class TypeBCRowMapper implements RowMapper<TypeBC> {

	@Override
	public TypeBC mapRow(ResultSet rs, int rowNum) throws SQLException {
		TypeBC orderType = new TypeBC();
		orderType.setProductNumber(sanitize(rs.getString("NoProduit")));
		orderType.setNumber(rs.getLong("NoTypeBC"));
		return orderType;
	}

}
