/*
 * Copyright(C) 2019 FUYUN DATA SERVICES CO.,LTD. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 该源代码版权归属福韵数据服务有限公司所有
 * 未经授权，任何人不得复制、泄露、转载、使用，否则将视为侵权
 */

package com.fydata.config;

public class EsIndexBean {
    private static final InheritableThreadLocal<String> indexNameLocal = new InheritableThreadLocal<>();

    public void setIndexName(String indexName) {
        indexNameLocal.set(indexName);
    }

    public String getIndexName() {
        if(indexNameLocal.get() != null) {
            return indexNameLocal.get();
        }
        return "default_index_fydata";
    }

}
