package com.fydata.dao;

import com.alibaba.fastjson.JSON;
import com.fydata.entity.Document;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xfgeng
 * @create 2019/12/10 19:13
 */
@Component
public class AttachmentDao extends EsRepositoryDao<Document> {

    public void checkAttachmentIndex(String indexName) {
        ElasticsearchRestTemplate restTemplate = this.getElasticsearchRestTemplate();
        if(this.esClientHelper.indexExists(indexName)) {
            return;
        }
        restTemplate.createIndex(indexName, super.buildSettings());
        Class handlerType = getEntityType();
        restTemplate.putMapping(indexName, handlerType.getSimpleName().toLowerCase(), this.buildAttachmentMapping());
    }

    /**
     * 构建mapping
     */
    public XContentBuilder buildAttachmentMapping() {
        XContentBuilder mapping = null;
        try {
            mapping = XContentFactory.jsonBuilder().startObject().startObject("properties");
            mapping.startObject("id")
                    .field("type", "keyword")
                    .endObject();
            mapping.startObject("orgName")
                    .field("type", "text")
                    .field("analyzer", "ik_max_word")
                    .endObject();

            Map<String,Object> car_number = new HashMap<>();
            car_number.put("type","text");
            car_number.put("analyzer","car_number_tokenizer");
            Map<String,Object> fields = new HashMap<>();
            fields.put("car_number",car_number);

            mapping.startObject("attachment.content")
                    .field("type", "text")
                    .field("analyzer", "ik_smart")
                    .field("fields",fields)
                    .endObject();

            mapping.endObject().endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapping;
    }

    /**
     * 添加数据，带附件
     * @param indexName
     * @param document
     */
    public void saveAttachment(String indexName, Document document) throws IOException {
        this.checkAttachmentIndex(indexName);
        IndexRequest indexRequest = new IndexRequest(indexName);
        indexRequest.source(JSON.toJSONString(document), XContentType.JSON);
        //上传同时，使用attachment pipline进行提取文件
        indexRequest.setPipeline("attachment");
        RestHighLevelClient client = super.esClientHelper.getRestHighLevelClient();
        client.index(indexRequest, RequestOptions.DEFAULT);
    }



}
