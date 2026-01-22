package com.poivredesiles.fundraising.service.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusCsvDTO implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@CsvBindByPosition(position = 0)
	@CsvBindByName(column = "orderNumber")
	private Long orderNumber;

	@CsvBindByPosition(position = 1)
	@CsvBindByName(column = "orderStatus")
	private String orderStatus;
}
