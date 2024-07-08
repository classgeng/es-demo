/*
 * Copyright(C) 2019 FUYUN DATA SERVICES CO.,LTD. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 该源代码版权归属福韵数据服务有限公司所有
 * 未经授权，任何人不得复制、泄露、转载、使用，否则将视为侵权
 */
package com.fydata.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(indexName = "#{esIndexBean.getIndexName()}", createIndex = false)
public class UserInfo {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Keyword)
    private String title;

    @Field(type = FieldType.Integer)
    private Integer age;

    @Field(type = FieldType.Date)
    private Date birthday;

    @Field(type = FieldType.Text, analyzer="ik_max_word", searchAnalyzer="ik_smart")
    private String address;

    @Field(type = FieldType.Integer)
    private Integer sex;

    @MultiField(mainField = @Field(name = "remark", type = FieldType.Text, analyzer = "ik_smart"), otherFields = {
            @InnerField(type = FieldType.Keyword, suffix = "keyword") })
    private String remark;
}
