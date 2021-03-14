package com.workflow.util;

import java.io.Serializable;
import java.util.List;

/**
 * @author 26223
 * @time 2016年10月10日
 * @email lukw@eastcom-sw.com
 */
public class DataGrid<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<T> rows;

    private long total;

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
