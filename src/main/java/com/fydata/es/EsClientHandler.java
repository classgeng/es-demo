/*
 * Copyright(C) 2019 FUYUN DATA SERVICES CO.,LTD. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 该源代码版权归属福韵数据服务有限公司所有
 * 未经授权，任何人不得复制、泄露、转载、使用，否则将视为侵权
 */


package com.fydata.es;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fydata.entity.EsEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class EsClientHandler {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 判断索引是否存在
     * @param indexName
     * @return
     */
    public boolean checkIndexExists(String indexName) {
        GetIndexRequest request = new GetIndexRequest(indexName);
        try {
            return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("判断索引是否存在，操作异常！");
        }
        return false;
    }

    /**
     * 创建索引(默认分片数为5和副本数为1)
     * @param indexName
     * @param settings
     * @param mapping
     * @throws IOException
     */
    public void checkOrCreateIndex(String indexName, Map<String,Object> settings, XContentBuilder mapping) throws IOException {
        if (checkIndexExists(indexName)) {
            log.debug("\"index={}\"索引已经存在！", indexName);
            return;
        }
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.settings(settings);
        request.mapping(mapping);
        CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        // 指示是否所有节点都已确认请求
        boolean acknowledged = response.isAcknowledged();
        // 指示是否在超时之前为索引中的每个分片启动了必需的分片副本数
        boolean shardsAcknowledged = response.isShardsAcknowledged();
        if (acknowledged || shardsAcknowledged) {
            log.debug("创建索引成功！索引名称为{}", indexName);
        }
    }

    /**
     * 保存数据
     * @param indexName
     * @param entity
     * @return
     */
    public void save(String indexName, EsEntity entity) throws IOException {
        this.checkOrCreateIndex(indexName,entity.buildSettings(),entity.buildMapping());
        IndexRequest request = new IndexRequest(indexName);
        String jsonStr = mapper.writeValueAsString(entity);
        request.source(jsonStr, XContentType.JSON);
        //立即刷新数据
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        log.info("response status:"+response.status().getStatus());
    }

    /**
     * 保存文档数据
     * @param indexName
     * @param entity
     * @return
     */
    public void saveDoc(String indexName, EsEntity entity) throws IOException {
        this.checkOrCreateIndex(indexName,entity.buildSettings(),entity.buildMapping());
        IndexRequest request = new IndexRequest(indexName);
        String jsonStr = mapper.writeValueAsString(entity);
        request.source(jsonStr, XContentType.JSON);
        //上传同时，使用attachment pipline进行提取文件
        request.setPipeline("attachment");
        //立即刷新数据
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        log.info("response status:"+response.status().getStatus());
    }

    /**
     * 按条件查询总记录数
     *
     * @param indexName        索引名
     * @param boolQueryBuilder 查询条件
     * @return 总记录数
     */
    public long countByQuery(String indexName, BoolQueryBuilder boolQueryBuilder) throws IOException {
        CountRequest countRequest = new CountRequest(indexName);
        countRequest.source(new SearchSourceBuilder().query(boolQueryBuilder));
        CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
        return countResponse.getCount();
    }

    /**
     * 查询数据
     * @param indexName
     * @param id
     * @return
     */
    public GetResponse getById(String indexName, String id) {
        GetResponse response = null;
        GetRequest request = new GetRequest(indexName, id);
        try {
            response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("根据ID获取索引数据，操作异常！");
        }
        return response;
    }


    /**
     * 查询数据
     * @param indexName
     * @param builder
     * @return
     */
    public SearchResponse search(String indexName,SearchSourceBuilder builder) {
        SearchResponse response = null;
        SearchRequest  request = new SearchRequest(indexName);
        request.source(builder);
        try {
            response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            log.info("response status:"+response.status().getStatus());
            return response;
        } catch (IOException e) {
            log.error("查询索引数据异常！");
        }
        return response;
    }

    public <T> List<T> search(String indexName, SearchSourceBuilder builder, Class<T> c){
        List<T> res = new ArrayList<>();
        //如果没有设置大小，则给个默认大小
        if(builder == null || builder.size() < 1){
            builder.size(10000);
        }
        SearchRequest request = new SearchRequest(indexName);
        request.scroll(TimeValue.timeValueSeconds(1L));
        request.source(builder);
        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            SearchHit[] both = hits;
            List<String> scrollIds = new ArrayList<>();
            String scrollId = response.getScrollId();
            scrollIds.add(scrollId);
            while (hits != null && hits.length > 0) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(TimeValue.timeValueSeconds(60L));
                response = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
                scrollId = response.getScrollId();
                hits = response.getHits().getHits();
                if (hits != null && hits.length > 0) {
                    scrollIds.add(scrollId);
                    both = (SearchHit[]) ArrayUtils.addAll(both, hits);
                }
            }
            ClearScrollRequest requests = new ClearScrollRequest();
            requests.setScrollIds(scrollIds);
            restHighLevelClient.clearScroll(requests, RequestOptions.DEFAULT);

            for (SearchHit hit : both) {
                res.add(mapper.readValue(hit.getSourceAsString(), c));
            }
        } catch (Exception e) {
            log.error("查询索引数据异常！");
        }
        return res;
    }


    /**
     * 修改数据
     * @param indexName
     * @param id
     * @param builder
     * @return
     */
    public UpdateResponse update(String indexName, String id, XContentBuilder builder) {
        UpdateResponse response = null;
        UpdateRequest request = new UpdateRequest(indexName,id).doc(builder);
        try {
            response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
            log.info("response status:"+response.status().getStatus());
            return response;
        } catch (IOException e) {
            log.error("修改索引数据异常！",e);
        }
        return response;
    }

    /**
     * 批量修改数据
     * @param indexName
     * @param script
     * @return
     */
    public void updateByQuery(String indexName, QueryBuilder queryBuilder, String script) {
        UpdateByQueryRequest updateRequest = new UpdateByQueryRequest(indexName);
        updateRequest.setQuery(queryBuilder);
        updateRequest.setScript(new Script(ScriptType.INLINE, "painless", script, Collections.emptyMap()));
        try {
            BulkByScrollResponse resp = restHighLevelClient.updateByQuery(updateRequest, RequestOptions.DEFAULT);
            System.out.println("Batches:"+resp.getBatches());
            System.out.println("Total:"+resp.getTotal());
        } catch (IOException e) {
            log.error("修改索引数据异常！",e);
        }
    }


    /**
     * 删除数据
     * @param indexName
     * @param id
     * @return
     */
    public DeleteResponse delete(String indexName, String id) {
        DeleteResponse response = null;
        DeleteRequest request = new DeleteRequest(indexName,id);
        try {
            response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
            log.info("response status:"+response.status().getStatus());
            return response;
        } catch (IOException e) {
            log.error("查询索引数据异常！");
        }
        return response;
    }

    public void close() throws IOException {
        this.restHighLevelClient.close();
    }

}

