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
import org.elasticsearch.common.xcontent.XContentBuilder;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
public abstract class EsEntity implements Serializable {

    /**
     * 主键
     */
    private String id;

    /**
     * 构建settings
     */
    public Map<String,Object> buildSettings() {
        Map<String,Object> map = new HashMap<>();
        map.put("max_result_window",Integer.MAX_VALUE);
        map.put("refresh_interval", "30s");
        return map;
    }

    /**
     * 构建mapping
     */
    public abstract XContentBuilder buildMapping() throws IOException;

}
