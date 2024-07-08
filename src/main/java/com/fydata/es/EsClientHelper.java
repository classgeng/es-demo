/*
 * Copyright(C) 2019 FUYUN DATA SERVICES CO.,LTD. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 该源代码版权归属福韵数据服务有限公司所有
 * 未经授权，任何人不得复制、泄露、转载、使用，否则将视为侵权
 */


package com.fydata.es;

import com.fydata.config.EsConfig;
import com.fydata.util.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class EsClientHelper {

    //ES连接池
    private static ConcurrentHashMap<String, RestHighLevelClient> esPool = new ConcurrentHashMap<>();

    @Autowired
    private EsConfig esConfig;
    @Autowired
    private MyElasticsearchConverter myElasticsearchConverter;
    @Autowired
    private MyEntityMapper myEntityMapper;

    /**
     * 获取默认链接
     * @return
     */
    public synchronized RestHighLevelClient getRestHighLevelClient(){
        String key = this.hashKey(esConfig);
        RestHighLevelClient restHighLevelClient = esPool.get(key);
        if (null == restHighLevelClient) {
            log.debug("当前ES连接数：{}", esPool.size());
            if(esPool.size()>3000){
                esPool.clear();
            }
            restHighLevelClient = this.createRestHighLevelClient(esConfig);
            esPool.put(key, restHighLevelClient);
        }
        return restHighLevelClient;
    }

    /**
     * 获取链接
     * @return
     */
    public synchronized RestHighLevelClient getRestHighLevelClient(EsConfig esConfig){
        String key = this.hashKey(esConfig);
        RestHighLevelClient restHighLevelClient = esPool.get(key);
        if (null == restHighLevelClient) {
            log.debug("当前ES连接数：{}", esPool.size());
            if(esPool.size()>3000){
                esPool.clear();
            }
            restHighLevelClient = this.createRestHighLevelClient(esConfig);
            esPool.put(key, restHighLevelClient);
        }
        return restHighLevelClient;
    }

    /**
     * 获取默认连接,案件库信息从当前用户获取
     * @return
     */
    public ElasticsearchRestTemplate getElasticsearchRestTemplate(){
        RestHighLevelClient client = this.getRestHighLevelClient();
        if(null == client){
            return null;
        }
        return new ElasticsearchRestTemplate(client,this.myElasticsearchConverter,this.myEntityMapper);
    }

    /**
     * 获取连接,案件库信息从当前用户获取
     * @return
     */
    public ElasticsearchRestTemplate getElasticsearchRestTemplate(EsConfig esConfig){
        RestHighLevelClient client = this.getRestHighLevelClient(esConfig);
        if(null == client){
            return null;
        }
        return new ElasticsearchRestTemplate(client,this.myElasticsearchConverter,this.myEntityMapper);
    }

    /**
     * 创建连接,案件库信息从当前用户获取
     * @return
     */
    private RestHighLevelClient createRestHighLevelClient(EsConfig esConfig){
        try {
            String[] nodeArr = esConfig.getNodes().split(",");
            HttpHost[] hosts = new HttpHost[nodeArr.length];
            for (int i=0;i<nodeArr.length;i++){
                String[] hostArr = nodeArr[i].split(":");
                hosts[i] = new HttpHost(hostArr[0],Integer.parseInt(hostArr[1]),esConfig.getScheme());
            }
            RestClientBuilder restClientBuilder = RestClient.builder(hosts);
            if(!StringUtils.isEmpty(esConfig.getUsername())) {
                //配置权限验证
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(esConfig.getUsername(), esConfig.getPassword()));
                restClientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                });
            }
            return new RestHighLevelClient(restClientBuilder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断索引是否存在
     * @param indexName
     * @return
     */
    public boolean indexExists(String indexName) {
        ElasticsearchRestTemplate esRestTemplate = this.getElasticsearchRestTemplate();
        return esRestTemplate.indexExists(indexName);
    }

    /**
     * 获取链接哈希值：模式+用户名+密码
     * @param esConfig
     * @return
     */
    private String hashKey(EsConfig esConfig){
        String keyStr = esConfig.getNodes()+esConfig.getUsername()+esConfig.getPassword();
        return DigestUtil.sha256(keyStr);
    }


}

