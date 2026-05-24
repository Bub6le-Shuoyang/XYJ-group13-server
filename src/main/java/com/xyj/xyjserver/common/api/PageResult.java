package com.xyj.xyjserver.common.api;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 分页响应格式
 * @param <T>
 */
@Data
public class PageResult<T> implements Serializable {
    private List<T> records;
    private Long total;
    private Long size;
    private Long current;
    private Long pages;

    public PageResult() {
    }

    public PageResult(List<T> records, Long total, Long size, Long current) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.current = current;
        this.pages = size == 0 ? 0 : (total % size == 0 ? total / size : (total / size) + 1);
    }
}