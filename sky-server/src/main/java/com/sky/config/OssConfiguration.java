package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OSS配置类，用于创建AliOssUtil Bean
 */
@Configuration
@Slf4j
public class OssConfiguration {

    @Autowired
    private AliOssProperties aliOssProperties;

    /**
     * 引入阿里云的依赖在application当中用占位符添加dev当中的密钥之类的信息，通过属性的配置文件类将属性封装成JAVA对象
     * 创建AliOssUtil Bean
     * @return
     */
    @Bean
    @ConditionalOnMissingBean//保证spring容器里面只有一个Util对象
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties ) {
        log.info("开始创建OSS工具类Bean：{}", aliOssProperties);
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
