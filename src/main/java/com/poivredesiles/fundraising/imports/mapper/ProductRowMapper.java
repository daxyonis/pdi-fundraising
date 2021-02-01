package com.poivredesiles.fundraising.imports.mapper;

import static com.poivredesiles.fundraising.imports.ImportsUtils.sanitize;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.poivredesiles.fundraising.imports.dto.Product;

public class ProductRowMapper implements RowMapper<Product> {

	@Override
	public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
		Product product = new Product();
		product.setNumber(sanitize(rs.getString("NoProduit")));		
		product.setSectionNum(rs.getLong("NoSection"));		
		product.setNameFr(sanitize(rs.getString("NomFr")));
		product.setNameEn(sanitize(rs.getString("NomEn")));
		product.setDescEn(sanitize(rs.getString("DescriptionEn")));
		product.setDescFr(sanitize(rs.getString("DescriptionFr")));
		product.setWeight(sanitize(rs.getString("Poids")));
		return product;
	}
	
}
