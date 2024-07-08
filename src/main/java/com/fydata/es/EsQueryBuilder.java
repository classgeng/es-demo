package com.fydata.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

/**
 * @author xfgeng
 * @date 2020/12/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EsQueryBuilder {

    private String[] fields;

    private BoolQueryBuilder queryBuilder;

    private HighlightBuilder highlightBuilder;

}
