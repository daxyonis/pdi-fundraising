package com.poivredesiles.fundraising.resource;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.poivredesiles.fundraising.resource.datatables.DataTablesRequest;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class OrdersRequest extends DataTablesRequest implements Serializable {

    @JsonProperty(required = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate startDate;
    @JsonProperty(required = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate endDate;
    @JsonProperty(required = false)
    private String status;

}
