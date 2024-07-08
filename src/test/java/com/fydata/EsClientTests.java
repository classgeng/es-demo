package com.fydata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fydata.entity.Document;
import com.fydata.entity.MultiSource;
import com.fydata.es.EsClientHandler;
import com.fydata.util.Base64Util;
import com.fydata.util.FileUtil;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsClientTests {

	private static final String INDEX_DOC_NAME = "document_index";
    private static final String INDEX_MS_NAME = "msdap_multisource";

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private EsClientHandler esClientHandler;

	@Test
	public void saveDocumentTest() throws IOException {
        //保存数据
        String filePath = "/Users/xfgeng/Desktop/TCE/ES分词测试.txt";
        byte[] content = FileUtil.readFileByByte(filePath);
        Document document = new Document();
        document.setId("111");
        document.setOrgName(filePath.substring(filePath.lastIndexOf("\\") + 1));
        document.setContent(Base64Util.stringToEncode(content));
        esClientHandler.saveDoc(INDEX_DOC_NAME, document);

	}

    @Test
    public void queryByPageTest() throws IOException {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.filter(QueryBuilders.termQuery("sourceType", "CCC"));

        long totleCount = esClientHandler.countByQuery(INDEX_MS_NAME, queryBuilder);
        System.out.println("totleCount:"+totleCount);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.from(20000);
        searchSourceBuilder.size(10);
        SearchResponse response = esClientHandler.search(INDEX_MS_NAME,searchSourceBuilder);

        //构建返回结果
        List<MultiSource> pageList = new ArrayList<>();
        SearchHits hits = response.getHits();
        hits.forEach(hit -> {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            MultiSource multiSource = mapper.convertValue(sourceAsMap, MultiSource.class);
            pageList.add(multiSource);
        });
        System.out.println("totleCount:"+hits.getTotalHits().value);
        pageList.forEach(item -> System.out.println(item.getSenderId()));
    }

    @Test
    public void queryAllTest() throws IOException {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.mustNot(QueryBuilders.existsQuery("sender"));
        queryBuilder.filter(QueryBuilders.termQuery("sourceType", "BILL"));

        long totleCount = esClientHandler.countByQuery(INDEX_MS_NAME, queryBuilder);
        System.out.println("totleCount:"+totleCount);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.size(100000);
        List<MultiSource> multiSources = esClientHandler.search(INDEX_MS_NAME, searchSourceBuilder, MultiSource.class);
        System.out.println("multiSources size:"+multiSources.size());
    }

    @Test
    public void updateByQueryTest() throws IOException {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.filter(QueryBuilders.termQuery("sourceType", "BILL"));
        //queryBuilder.filter(QueryBuilders.termQuery("sender",""));
        queryBuilder.filter(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("sender")));

        /*BoolQueryBuilder boolQueryBuilder1 = QueryBuilders.boolQuery();
        boolQueryBuilder1.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("sender")));
        boolQueryBuilder1.should(QueryBuilders.termQuery("sender",""));
        queryBuilder.filter(boolQueryBuilder1);*/

        //queryBuilder.filter(QueryBuilders.wildcardQuery("sender","*"));

        BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery();
        boolQueryBuilder2.should(QueryBuilders.termsQuery("senderId", "6217003250006907362"));
        boolQueryBuilder2.should(QueryBuilders.termQuery("senderAccount", "6217003250006907362"));
        boolQueryBuilder2.should(QueryBuilders.termQuery("bfkh", "6217003250006907362"));
        queryBuilder.filter(boolQueryBuilder2);

        long totleCount = esClientHandler.countByQuery(INDEX_MS_NAME, queryBuilder);
        System.out.println("totleCount:"+totleCount);

        String script = "ctx._source.sender = 'm1778'";
        esClientHandler.updateByQuery(INDEX_MS_NAME,queryBuilder,script);
    }


    @Test
    public void queryTest() throws IOException {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.filter(QueryBuilders.matchPhraseQuery("attachment.content",""));

        long totleCount = esClientHandler.countByQuery(INDEX_MS_NAME, null);
        System.out.println("totleCount:"+totleCount);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.from(20000);
        searchSourceBuilder.size(10);
        SearchResponse response = esClientHandler.search(INDEX_MS_NAME,searchSourceBuilder);

        //构建返回结果
        List<MultiSource> pageList = new ArrayList<>();
        SearchHits hits = response.getHits();
        hits.forEach(hit -> {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            MultiSource multiSource = mapper.convertValue(sourceAsMap, MultiSource.class);
            pageList.add(multiSource);
        });
        System.out.println("totleCount:"+hits.getTotalHits().value);
        pageList.forEach(item -> System.out.println(item.getSenderId()));
    }

    @Test
    public void queryCountTest() throws IOException {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termsQuery("sourceType", "XM-HT"));
        long count = esClientHandler.countByQuery(INDEX_DOC_NAME, boolQuery);
        System.out.println(count);
    }


}
