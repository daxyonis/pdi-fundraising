package com.poivredesiles.fundraising.imports.mapper;

import static com.poivredesiles.fundraising.imports.ImportsUtils.sanitize;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.poivredesiles.fundraising.imports.dto.Campaign;

public class CampaignRowMapper implements RowMapper<Campaign> {

	@Override
	public Campaign mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Campaign campaign = new Campaign();
		campaign.setNumber(rs.getLong("NoCampagne"));		
		campaign.setOrganizationNumber(sanitize(rs.getString("NoOrganisme")));
		campaign.setOrganizationName(sanitize(rs.getString("NomOrganisme")));
		campaign.setProject(sanitize(rs.getString("Projet")));
		campaign.setLeaderNumber(sanitize(rs.getString("NoResponsable")));
		campaign.setLeaderEmail(sanitize(rs.getString("CourrielResponsable")));
		campaign.setDueDate(rs.getString("DateLimite"));
		campaign.setNumTypeBC(rs.getLong("NoTypeBC"));
		campaign.setBlockedAsString(rs.getString("Bloqué"));		
		campaign.setClosedDate(rs.getString("DateTerminée"));
		return campaign;
	}	

}
