package com.fydata.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author xfgeng
 * @date 2018/10/11
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
public class EsConfig {

    private String scheme;

    private String nodes;

    private String username;

    private String password;

}
