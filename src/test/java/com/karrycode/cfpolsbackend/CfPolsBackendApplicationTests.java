package com.karrycode.cfpolsbackend;

import com.karrycode.cfpolsbackend.config.WebSocketConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = WebSocketConfig.class)
public class CfPolsBackendApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("Hello World");
    }

}
