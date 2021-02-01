package com.poivredesiles.fundraising.imports.mapper;

import static com.poivredesiles.fundraising.imports.ImportsUtils.sanitize;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.poivredesiles.fundraising.imports.dto.Section;

public class SectionRowMapper implements RowMapper<Section> {

	@Override
	public Section mapRow(ResultSet rs, int rowNum) throws SQLException {
		Section section = new Section();
		section.setNumber(rs.getLong("NoSection"));
		section.setUnitPrice(BigDecimal.valueOf(rs.getLong("PrixUnitaire")));
		section.setSectionEn(sanitize(rs.getString("SectionEn")));
		section.setSectionFr(sanitize(rs.getString("SectionFr")));
		return section;
	}

}
