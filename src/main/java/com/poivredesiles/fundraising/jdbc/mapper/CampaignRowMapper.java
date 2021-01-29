package com.poivredesiles.fundraising.jdbc.mapper;

import static com.poivredesiles.fundraising.jdbc.JdbcUtils.sanitize;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.poivredesiles.fundraising.jdbc.dto.Campaign;

public class CampaignRowMapper implements RowMapper<Campaign> {

	@Override
	public Campaign mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Campaign campaign = new Campaign();
		campaign.setOrganizationNumber(sanitize(rs.getString("NoOrganisme")));
		campaign.setOrganizationName(sanitize(rs.getString("NomOrganisme")));
		campaign.setProject(sanitize(rs.getString("Projet")));
		campaign.setLeaderNumber(sanitize(rs.getString("NoResponsable")));
		campaign.setLeaderEmail(sanitize(rs.getString("CourrielResponsable")));
		campaign.setDueDate(rs.getDate("DateLimite"));
		campaign.setNumTypeBC(rs.getLong("NoTypeBC"));
		campaign.setBlocked(sanitize(rs.getString("Bloqué")));
		campaign.setClosedDate(rs.getDate("DateTerminée"));
		return campaign;
	}

}
