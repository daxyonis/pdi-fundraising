package com.poivredesiles.fundraising.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poivredesiles.fundraising.resource.datatables.DataTablesRequest;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class OrdersRequest extends DataTablesRequest {

    @JsonProperty(required = false)
    private LocalDate startDate;
    @JsonProperty(required = false)
    private LocalDate endDate;
    @JsonProperty(required = false)
    private String status;

}
