package com.poivredesiles.fundraising.jdbc.mapper;

import static com.poivredesiles.fundraising.jdbc.JdbcUtils.sanitize;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.poivredesiles.fundraising.jdbc.dto.Seller;

public class SellerRowMapper implements RowMapper<Seller> {

	@Override
	public Seller mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Seller seller = new Seller();
		seller.setNumber(rs.getLong("NoVendeur"));
		seller.setName(sanitize(rs.getString("NomVendeur")));
		seller.setBuyerCode(sanitize(rs.getString("CodeAcheteur")));
		seller.setCampaignCode(sanitize(rs.getString("CodeCampagne")));
		seller.setPassword(sanitize(rs.getString("MotDePasse")));
		seller.setAuthorization(sanitize(rs.getString("Autorisation")));
		return seller;
	}

}
