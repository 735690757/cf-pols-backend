package com.karrycode.cfpolsbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2024/11/20 下午1:08
 * @PackageName com.karrycode.springbootminio.config
 * @ClassName MinioInfo
 * @Description minio配置信息
 * @Version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioInfo {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;

}
