package com.karrycode.cfpolsbackend.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/5/10 22:33
 * @PackageName com.karrycode.cfpolsbackend.common
 * @ClassName MyDbProperties
 * @Description
 * @Version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "my.db")
public class MyDbProperties {
    private String host;
    private String user;
    private String password;
}
