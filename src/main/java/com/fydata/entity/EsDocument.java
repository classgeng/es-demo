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
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(indexName = "#{esIndexBean.getIndexName()}", createIndex = false)
public class EsDocument implements Serializable {

    @Id
    @Field(type = FieldType.Keyword)
    private String zl_dmq1001;

    @Field(type = FieldType.Keyword)
    private String sjsjdccjmbbh;

    @Field(type = FieldType.Keyword)
    private String bddz;

    @Field(type = FieldType.Date)
    private Date zl_qzksjc;

    @Field(type = FieldType.Date)
    private Date zl_hcksjc;

    @Field(type = FieldType.Double)
    private Integer zl_score;

    @Field(type = FieldType.Keyword)
    private String zl_xzqh;

    @Field(type = FieldType.Keyword)
    private String sczt;

    /**
     * 删除时间
     */
    @Field(type = FieldType.Long)
    private Long scsj;

    /**
     *  用户id
     */
    @Field(type = FieldType.Keyword)
    private String user_id;

    /**
     *  任务id
     */
    @Field(type = FieldType.Keyword)
    private String task_id;

    /**
     *  鉴权状态（0：无需鉴权，1：待申请，2：申请中，3：申请成功，4：申请失败）
     */
    @Field(type = FieldType.Keyword)
    private Integer authStatus;

    private Integer thingMark;

    private Integer personnelTag;

    /**
     * hashId
     */
    @Field(type = FieldType.Keyword)
    private String groupId;

}
