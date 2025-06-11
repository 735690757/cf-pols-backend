package com.karrycode.cfpolsbackend.config;

import io.minio.MinioClient;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/1/11 上午12:13
 * @PackageName com.karrycode.miniostu.config
 * @ClassName ConfigMinio
 * @Description minio配置
 * @Version 1.0
 */
@Configuration
public class ConfigMinio {
    @Resource
    private MinioInfo minioInfo;
    // 单例的MinioClient，有没有线程安全问题？答案是没有线程安全问题。
    @Bean
    public MinioClient minioClient(){
        // 链式编程，构建器模式
        return  MinioClient.builder()
                .endpoint(minioInfo.getEndpoint())
                .credentials(minioInfo.getAccessKey(),minioInfo.getSecretKey())
                .build();
    }
}
