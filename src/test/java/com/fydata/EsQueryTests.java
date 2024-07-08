package com.fydata;

import com.fydata.dao.MultiSourceDao;
import com.fydata.entity.MultiSource;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsQueryTests {

    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";
	private static final String INDEX_NAME = "aj_42000020200430_multisource_fydata";

    private static final List<String> cjbhList = new ArrayList<>();

    static {
        cjbhList.add("420000201900101009aa");
        cjbhList.add("420000201900101022");
    }

    @Autowired
    private MultiSourceDao multiSourceDao;

	@Test
	public void searchTest() {
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        cjbhList.forEach(item -> {
            queryBuilder.should(QueryBuilders.prefixQuery("sjsjdccjmbbh",item));
        });
        Iterable<MultiSource> list = multiSourceDao.search(INDEX_NAME, queryBuilder);
        list.forEach(item -> {
			System.out.println(item.getSjsjdccjmbbh() + " -> " + item.getZl_dmq1001());
		});
	}

    @Test
    public void searchByPageTest() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.filter(QueryBuilders.termQuery("sourceType", "QZ"));

        BoolQueryBuilder cjbhQuery = QueryBuilders.boolQuery();
        cjbhList.forEach(item -> {
            cjbhQuery .should(QueryBuilders.prefixQuery("sjsjdccjmbbh",item));
        });
        queryBuilder.filter(cjbhQuery);

        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();
        searchQuery.withQuery(queryBuilder);
        searchQuery.withPageable(PageRequest.of(0, 1000));

       /* Page<MultiSource> page = multiSourceDao.searchByPage(INDEX_NAME, searchQuery.build());
        System.out.println(page.getTotalElements()+ " -> " +page.getTotalPages());
        page.forEach(item -> {
            System.out.println(item.getSjsjdccjmbbh() + " -> " + item.getSourceType() + " -> " + item.getZl_dmq1001());
        });*/
    }


    @Test
    public void searchGroupTest() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termsQuery("sourceType", new String[]{"YY","WD"}));
        //queryBuilder.must(QueryBuilders.termQuery("sourceType", "YY"));

        /*TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("senderId").field("senderId");
        aggregationBuilder.subAggregation(AggregationBuilders.terms("sender").field("sender"));*/
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("receiverId").field("receiverId");
        //aggregationBuilder.subAggregation(AggregationBuilders.terms("receiver").field("receiver"));
        aggregationBuilder.executionHint("map");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.aggregation(aggregationBuilder);

        SearchResponse response = multiSourceDao.search(INDEX_NAME,searchSourceBuilder);
        Aggregations aggregations = response.getAggregations();
        Terms agg = aggregations.get("receiverId");

        List<? extends Terms.Bucket> buckets = agg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            System.out.println(bucket.getKeyAsString() + "->" + bucket.getDocCount());
        }

    }

}
