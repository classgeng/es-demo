package com.fydata.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fydata.config.EsConfig;
import com.fydata.config.EsIndexBean;
import com.fydata.es.*;
import com.fydata.util.TypeUtil;
import com.fydata.vo.PageVO;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.support.AbstractElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import java.io.IOException;
import java.util.*;

/**
 * @author xfgeng
 * @create 2019/12/13 14:35
 */
@Component
public class EsRepositoryDao<E> extends AbstractElasticsearchRepository<E, String> {

    @Autowired
    protected EsIndexBean esIndexBean;
    @Autowired
    private EsConfig esConfig;
    @Autowired
    private EsEntityInformation esEntityInformation;
    @Autowired
    protected EsClientHelper esClientHelper;
    @Autowired
    private ObjectMapper mapper;

    @Override
    protected String stringIdRepresentation(String s) {
        return s;
    }

    /**
     * 获取动态链接信息
     */
    protected RestHighLevelClient getRestHighLevelClient(){
        return esClientHelper.getRestHighLevelClient(esConfig);
    }

    /**
     * 获取动态链接信息
     */
    protected ElasticsearchRestTemplate getElasticsearchRestTemplate(){
        return esClientHelper.getElasticsearchRestTemplate(esConfig);
    }

    /**
     * 初始化动态链接信息
     */
    protected void initElasticsearch(){
        super.setElasticsearchOperations(this.getElasticsearchRestTemplate());
        ElasticsearchEntityInformation<E, String> entityInformation = esEntityInformation.getEntityInformation(getEntityType());
        super.entityInformation = entityInformation;
        super.setEntityClass(getEntityType());
    }

    protected Class<E> getEntityType() {
        return (Class)TypeUtil.getSuperclassTypeParameter(this.getClass());
    }

    public void checkIndex(String indexName) {
        ElasticsearchRestTemplate restTemplate = this.getElasticsearchRestTemplate();
        if(this.esClientHelper.indexExists(indexName)) {
           return;
        }
        restTemplate.createIndex(indexName, this.buildSettings());
        Class handlerType = getEntityType();
        restTemplate.putMapping(indexName, handlerType.getSimpleName().toLowerCase(), handlerType);
    }

    /**
     * 构建settings
     */
    public Map<String,Object> buildSettings() {
        Map<String,Object> map = new HashMap<>();
        map.put("max_result_window",Integer.MAX_VALUE);
        map.put("highlight.max_analyzed_offset", Integer.MAX_VALUE);
        map.put("refresh_interval", "1s");
        //自定义分词器
        map.put("analysis.analyzer.car_number_tokenizer.tokenizer", "car_number_tokenizer");
        map.put("analysis.tokenizer.car_number_tokenizer.type", "pattern");
        map.put("analysis.tokenizer.car_number_tokenizer.pattern", "([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][a-zA-Z][a-hA-Hj-nJ-Np-zP-Z0-9]{4}[a-hA-Hj-nJ-Np-zP-Z0-9挂学警港澳])");
        map.put("analysis.tokenizer.car_number_tokenizer.group", 1);
        return map;
    }

    /**
     * 添加数据
     * @param indexName
     * @param esDocument
     */
    public void save(String indexName,E esDocument){
        esIndexBean.setIndexName(indexName);
        this.checkIndex(indexName);
        this.initElasticsearch();
        super.save(esDocument);
    }

    /**
     * 批量添加数据
     * @param indexName
     * @param list
     */
    public void saveAll(String indexName, List<E> list){
        this.checkIndex(indexName);
        esIndexBean.setIndexName(indexName);
        this.initElasticsearch();
        super.saveAll(list);
    }

    /**
     * 删除单条数据
     * @param indexName
     * @param id
     */
    public void deleteById(String indexName, String id){
        if(this.esClientHelper.indexExists(indexName)) {
            esIndexBean.setIndexName(indexName);
            this.initElasticsearch();
            super.deleteById(id);
        }
    }

    /**
     * 根据条件批量删除
     * @param indexName
     * @param builder
     */
    public void deleteByQuery(String indexName, QueryBuilder builder) {
        if(!this.esClientHelper.indexExists(indexName)) {
            return;
        }
        DeleteByQueryRequest request = new DeleteByQueryRequest(indexName);
        request.setQuery(builder);
        try {
            RestHighLevelClient restHighLevelClient = this.getRestHighLevelClient();
            restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取单条数据
     * @param indexName
     * @param id
     */
    public Optional<E> findById(String indexName, String id){
        if(!this.esClientHelper.indexExists(indexName)) {
            return null;
        }
        esIndexBean.setIndexName(indexName);
        this.initElasticsearch();
        return super.findById(id);
    }


    /**
     * 查询数据
     * @param indexName
     * @param queryBuilder
     * @return
     */
    public Iterable<E> search(String indexName,QueryBuilder queryBuilder){
        if(!this.esClientHelper.indexExists(indexName)) {
            return null;
        }
        esIndexBean.setIndexName(indexName);
        this.initElasticsearch();
        return super.search(queryBuilder);
    }

    /**
     * 查询数据
     * @param indexName
     * @param searchSourceBuilder
     * @return
     */
    public SearchResponse search(String indexName, SearchSourceBuilder searchSourceBuilder){
        if(!this.esClientHelper.indexExists(indexName)) {
            return null;
        }
        SearchResponse response = null;
        SearchRequest searchRequest = new SearchRequest(indexName).source(searchSourceBuilder);
        try {
            response = this.getRestHighLevelClient().search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 查询数据
     * @param indexName
     * @param esQueryBuilder
     * @return
     */
    public List<E> searchByPage(String indexName, EsQueryBuilder esQueryBuilder){
        if(!this.esClientHelper.indexExists(indexName)) {
            return null;
        }
        //构建查询
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.fetchSource(esQueryBuilder.getFields(),null);
        searchSourceBuilder.query(esQueryBuilder.getQueryBuilder());
        searchSourceBuilder.highlighter(esQueryBuilder.getHighlightBuilder());
        SearchResponse response = this.search(indexName,searchSourceBuilder);
        //构建返回结果
        List<E> list = new ArrayList<>();
        SearchHits hits = response.getHits();
        hits.forEach(hit -> {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            E object = mapper.convertValue(sourceAsMap, getEntityType());
            list.add(object);
        });
        return list;
    }

    /**
     * 查询数据
     * @param indexName
     * @param esQueryBuilder
     * @return
     */
    public PageVO<E> searchByPage(String indexName, EsQueryBuilder esQueryBuilder, SortBuilder sortBuilder, PageBuilder pageBuilder){
        if(!this.esClientHelper.indexExists(indexName)) {
            return null;
        }
        //构建查询
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.fetchSource(esQueryBuilder.getFields(),null);
        searchSourceBuilder.query(esQueryBuilder.getQueryBuilder());
        searchSourceBuilder.highlighter(esQueryBuilder.getHighlightBuilder());
        searchSourceBuilder.sort(sortBuilder);
        if(!ObjectUtils.isEmpty(pageBuilder)) {
            searchSourceBuilder.from(pageBuilder.getPageNum() - 1);
            searchSourceBuilder.size(pageBuilder.getPageSize());
        }
        SearchResponse response = this.search(indexName,searchSourceBuilder);
        //构建返回结果
        List<E> pageList = new ArrayList<>();
        SearchHits hits = response.getHits();
        hits.forEach(hit -> {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            E object = mapper.convertValue(sourceAsMap, getEntityType());
            pageList.add(object);
        });
        PageVO<E> pageVO = new PageVO();
        pageVO.setTotalCount(hits.getTotalHits().value);
        pageVO.setPageList(pageList);
        return pageVO;
    }

}
