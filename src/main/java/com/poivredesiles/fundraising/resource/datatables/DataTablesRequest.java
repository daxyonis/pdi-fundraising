package com.poivredesiles.fundraising.resource.datatables;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataTablesRequest implements Serializable {

    @Getter
    @Setter
    public static class Column implements Serializable {
        private String data;
        private String name;
        private boolean searchable;
        private boolean orderable;
        private Search search;
        public Column() {
            search = new Search();
        }
    }

    @Getter
    @Setter
    public static class Search implements Serializable{
        private String value;
        private boolean regex;
        public Search() {
            value = "";
            regex = false;
        }
    }

    @Getter
    @Setter
    public static class Order implements Serializable{
        private int column;
        private String dir;
        private String name;
        public Order() {
            name = "";
            column = 0;
        }
    }

    @JsonProperty("columns")
    private List<Column> columns = new ArrayList<>();

    @JsonProperty("draw")
    private int draw;

    @JsonProperty("length")
    private int length;

    @JsonProperty("order")
    private List<Order> order = new ArrayList<>();

    @JsonProperty("search")
    private Search search = new Search();

    @JsonProperty("start")
    private int start;

    public DataTablesRequest() {
    }

    public Sort getSort() {
        if (order == null || order.isEmpty()) {
            return Sort.unsorted();
        }
        Order o = order.get(0);
        String columnName;
        if (columns != null && !columns.isEmpty() && o.getColumn() < columns.size()) {
            columnName = columns.get(o.getColumn()).getData();
        } else if (o.getName() != null && !o.getName().isEmpty()) {
            columnName = o.getName();
        } else {
            return Sort.unsorted();
        }
        return Sort.by(Sort.Direction.fromString(o.getDir()), columnName);
    }

}


