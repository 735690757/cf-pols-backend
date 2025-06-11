package com.karrycode.cfpolsbackend.config;

import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/1/14 21:42
 * @PackageName com.karrycode.cfpolsbackend.config
 * @ClassName MongoDBConfig
 * @Description MongoDB配置
 * @Version 1.0
 */
@Configuration
public class MongoDBConfig {
    @Value("${spring.data.mongodb.url}")
    private String mongoUrl;
    @Value("${spring.data.mongodb.database.pols}")
    private String pols;

    @Primary
    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(
                new SimpleMongoClientDatabaseFactory(MongoClients.create(mongoUrl), pols));
    }
}
