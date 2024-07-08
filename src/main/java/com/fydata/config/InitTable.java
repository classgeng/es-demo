/*
 * Copyright(C) 2019 FUYUN DATA SERVICES CO.,LTD. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 该源代码版权归属福韵数据服务有限公司所有
 * 未经授权，任何人不得复制、泄露、转载、使用，否则将视为侵权
 */
package com.fydata.config;


import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Objects;

@Log4j2
@Component
public class InitTable {

    private static String dbSchema = "MSDM";

    @Autowired
    private TableConfig tableConfig;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void create() {
        String[] types = {"TABLE"};
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            for (Map.Entry<String, String> entry : tableConfig.getTables().entrySet()) {
                try (ResultSet rs = connection.getMetaData().getTables(null, dbSchema, entry.getKey(), types)) {
                    if (!rs.next()) {
                        log.info("本次新创建表：{}",entry.getKey());
                        jdbcTemplate.execute(entry.getValue());
                    }
                }
            }
            log.info("初始化创建表成功！");
        } catch (Exception e) {
            log.error("初始化创建表异常", e);
        }
    }

}
