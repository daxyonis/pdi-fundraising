package com.poivredesiles.fundraising.service.mapper;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import com.poivredesiles.fundraising.imports.ImportsUtils;

@Mapper
public interface CurrencyFormattingMapper {

	@Named("formatCurrency")
    public static String formatCurrency(BigDecimal amount) {  
    	return ImportsUtils.formatCurrency(amount);
    }
}
