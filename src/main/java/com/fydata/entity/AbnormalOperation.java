package com.fydata.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(indexName = "#{esIndexBean.getIndexName()}", createIndex = false)
public class AbnormalOperation {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Keyword)
    private String removeDate;

    @Field(type = FieldType.Keyword)
    private String data_mutate_timestamp;

    @Field(type = FieldType.Keyword)
    private String putReason;

    @Field(type = FieldType.Keyword)
    private String createTime;

    @Field(type = FieldType.Keyword)
    private String putDepartment;

    @Field(type = FieldType.Keyword)
    private String removeDepartment;

    @Field(type = FieldType.Keyword)
    private String removeReason;

    @Field(type = FieldType.Keyword)
    private String source;

    @Field(type = FieldType.Keyword)
    private String dw_import_time;

    @Field(type = FieldType.Keyword)
    private String putDate;

    @Field(type = FieldType.Keyword)
    private String cid;

    @Field(type = FieldType.Keyword)
    private String batch;

    @Field(type = FieldType.Keyword)
    private String crawl_time;

    @Field(type = FieldType.Keyword)
    private String dw_day;

    @Field(type = FieldType.Keyword)
    private String row_num;

}
