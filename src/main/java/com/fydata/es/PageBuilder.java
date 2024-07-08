package com.fydata.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageBuilder {

    /**
     * 当前页
     */
    private int pageNum = 1;

    /**
     * 页大小
     */
    private int pageSize = 10;

}
