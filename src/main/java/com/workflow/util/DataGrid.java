package com.workflow.util;

import java.io.Serializable;
import java.util.List;

/**
 * @author : yangqi
 * @email : lukewei@mockuai.com
 * @description :
 * @since : 2021/3/16 22:46
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
