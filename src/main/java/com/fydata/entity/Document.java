/*
 * Copyright(C) 2019 FUYUN DATA SERVICES CO.,LTD. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 该源代码版权归属福韵数据服务有限公司所有
 * 未经授权，任何人不得复制、泄露、转载、使用，否则将视为侵权
 */
package com.fydata.entity;

import com.fydata.constant.AppConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Document extends EsEntity {

    private String orgName;

    private String serName;

    private String type;

    private Long size;

    private String title;

    private String content;

    private Date createTime;

    private String createBy;

    @Override
    public Map<String,Object> buildSettings() {
        Map<String,Object> map = new HashMap<>();
        map.put("max_result_window",Integer.MAX_VALUE);
        map.put("highlight.max_analyzed_offset", Integer.MAX_VALUE);
        map.put("refresh_interval", "30s");
        //自定车牌义分词器(车牌号)
        map.put("analysis.analyzer.car_number_tokenizer.tokenizer", "car_number_tokenizer");
        map.put("analysis.tokenizer.car_number_tokenizer.type", "pattern");
        map.put("analysis.tokenizer.car_number_tokenizer.pattern", AppConstants.CAR_CARD_REGEXP);
        map.put("analysis.tokenizer.car_number_tokenizer.group", 1);
        return map;
    }

    @Override
    public XContentBuilder buildMapping() throws IOException {
        //附件内容（支持两种分词模式）
        Map<String,Object> car_number = new HashMap<>();
        car_number.put("type","text");
        car_number.put("analyzer","car_number_tokenizer");
        Map<String,Object> fields = new HashMap<>();
        fields.put("car_number",car_number);

        XContentBuilder mapping = XContentFactory.jsonBuilder().startObject().startObject("properties")
                .startObject("id")
                .field("type", "keyword")
                .endObject()
                .startObject("orgName")
                .field("type", "text")
                .field("analyzer", "ik_max_word")
                .endObject()
                .startObject("serName")
                .field("type", "keyword")
                .endObject()
                .startObject("attachment.content")
                .field("type", "text")
                .field("analyzer", "ik_smart")
                .field("fields",fields)
                .endObject()
                .startObject("type")
                .field("type", "keyword")
                .endObject()
                .startObject("size")
                .field("type", "long")
                .endObject()
                .startObject("createBy")
                .field("type", "keyword")
                .endObject()
                .startObject("createTime")
                .field("type", "date")
                .endObject()
                .endObject().endObject();
        return mapping;
    }
}
