package com.poivredesiles.fundraising.resource.datatables;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class DataTablesResponse<T> {
    private int draw;
    private long recordsTotal;
    private long recordsFiltered;
    private List<T> data;
    private String error;

    public DataTablesResponse(int draw, long recordsTotal, long recordsFiltered, List<T> data) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.data = data;
    }

    public DataTablesResponse(Page<T> page, int draw) {
        this.draw = draw;
        this.recordsTotal = page.getTotalElements();
        this.recordsFiltered = page.getTotalElements();
        this.data = page.getContent();
    }
}
