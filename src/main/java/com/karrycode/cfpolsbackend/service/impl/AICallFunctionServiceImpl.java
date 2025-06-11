package com.karrycode.cfpolsbackend.service.impl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/2/11 11:31
 * @PackageName com.karrycode.cfpolsbackend.service.impl
 * @ClassName AICallFunctionServiceImpl
 * @Description
 * @Version 1.0
 */
@Configuration
public class AICallFunctionServiceImpl {
    public record ConSayHello(String name) {
    }

    @Bean
    @Description("说你好")
    public Function<ConSayHello,String> sayHello() {
        return sayHello -> {
            System.out.println("hello !!!!!!!!!!!!!!!!!!!!!!!!!");
            return "hello " + sayHello.name();
        };
    }

}
