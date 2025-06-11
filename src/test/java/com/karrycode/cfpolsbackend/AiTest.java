package com.karrycode.cfpolsbackend;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import reactor.core.publisher.Flux;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/2/9 15:05
 * @PackageName com.karrycode.cfpolsbackend
 * @ClassName AiTest
 * @Description
 * @Version 1.0
 */

public class AiTest extends CfPolsBackendApplicationTests{
    @Resource
    private OpenAiChatClient openAiChatClient;

    /**
     * SpringAi测试
     */
    @Test
    public void testString(){
        String prompt = "你是谁？";
        String result = openAiChatClient.call(prompt);
        System.out.println(result);
    }

    /**
     * prompt测试
     */
    @Test
    public void testPrompt(){
        ChatResponse chatResponse = openAiChatClient.call(new Prompt("你是谁？", OpenAiChatOptions.builder()
                .withTemperature(0.5f).build()));
        System.out.println(chatResponse);
    }

    /**
     * stream方法测试
     */
    @Test
    public void testStream(){
        Flux<ChatResponse> stream = openAiChatClient.stream(new Prompt("你是谁？", OpenAiChatOptions.builder()
                .withTemperature(0.5f).build()));
        stream.subscribe(chatResponse -> {
            System.out.println(chatResponse.getResults());
        });
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void test2(){
        System.out.println("你好");
    }
}
