package com.poivredesiles.fundraising.resource.datatables;
import lombok.Data;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class DataTablesRequest {

    @Data
    public static class Column {
        private String data;
        private String name;
        private boolean searchable;
        private boolean orderable;
        private Search search;
    }

    @Data
    public static class Search {
        private String value;
        private boolean regex;
    }

    @Data
    public static class Order {
        private int column;
        private String dir;
        private String name;
    }

    private int draw = 0;
    private List<Column> columns = new ArrayList<>();
    private List<Order> order = new ArrayList<>();
    private Search search = new Search();
    private int start = 0;
    private int length = 10;

    public Sort getSort() {
        if (order == null || order.isEmpty()) {
            return Sort.unsorted();
        }
        Order o = order.get(0);
        Column c = columns.get(o.getColumn());
        return Sort.by(Sort.Direction.fromString(o.getDir()), c.getData());
    }

}


