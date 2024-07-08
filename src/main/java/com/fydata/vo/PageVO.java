package com.fydata.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PageVO<T> {

    public PageVO(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    private int pageNum;

    private int pageSize;

    private List<T> pageList;

    private long totalCount;



}
