package com.karrycode.cfpolsbackend.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.karrycode.cfpolsbackend.domain.vo.ChatMessageVO;
import com.karrycode.cfpolsbackend.service.impl.AICallFunctionServiceImpl;
import com.karrycode.cfpolsbackend.service.impl.KnowledgeBaseService;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/2/9 15:38
 * @PackageName com.karrycode.cfpolsbackend.controller
 * @ClassName AIChatController
 * @Description
 * @Version 1.0
 */
@RestController
@RequestMapping("/aiv1")
@CrossOrigin
public class AIChatController {
    @Resource
    private OpenAiChatClient openAiChatClient;
    @Resource
    private KnowledgeBaseService knowledgeBaseService;
    // 存储每个用户的聊天历史
    private final Map<String, List<ChatMessageVO>> userHistories = new ConcurrentHashMap<>();
    // aiCallFunctionService



    /**
     * @param openAiChatClient      初始化ai
     * @param knowledgeBaseService  模拟知识库
     * @param aiCallFunctionService 函数调用
     */
    public AIChatController(OpenAiChatClient openAiChatClient, KnowledgeBaseService knowledgeBaseService, AICallFunctionServiceImpl aiCallFunctionService) {
        this.openAiChatClient = openAiChatClient;
        this.knowledgeBaseService = knowledgeBaseService;
    }

    private static boolean isFunctionEnableis = false;

    /**
     * 初始化函数
     *
     * @param uid 用户id
     */
    public void initFunction(String uid) {
        if (isFunctionEnableis) return;
//        ArrayList<ChatMessageVO> systemPromptPre = new ArrayList<>();
//        systemPromptPre.add(new ChatMessageVO("system", "当前最受欢迎，热度最高的课程是Java SE 基础精讲，链接是：http://localhost:5173/student/courseDetail?id=20"));
//        userHistories.put(String.valueOf(uid),systemPromptPre);
        isFunctionEnableis = true;
    }

    /**
     * 流式聊天
     *
     * @param message   用户输入
     * @param agentName 智能体名
     * @param token     token
     * @return 响应流
     */
    @ApiOperation("聊天机器人")
    @GetMapping(value = "/chat-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestParam String message,
                                   @RequestParam( defaultValue = "客服小瑞") String agentName,
                                   @RequestParam String token) {
        String uid = (String) StpUtil.getLoginIdByToken(token);

        // 初始化函数
        initFunction(uid);

        // 获取或初始化用户历史记录
        List<ChatMessageVO> history = userHistories.computeIfAbsent(uid, k -> new ArrayList<>());
        System.out.println("历史记录：" + history);
        // 检索知识库
        String knowledge = knowledgeBaseService.retrieveRelevantKnowledge(message);
        System.out.println("知识库：" + knowledge);

        // 增强提示（RAG）
        String promptRAG = (knowledge.isEmpty() ? "" : "参考知识库信息：" + knowledge + "\n")
                + "用户问题：" + message;

        // 获取当前日期
        String currentDate = LocalDate.now().toString();

        // 构建系统提示，包含动态日期
        String systemPrompt = String.format("你是%s，一名专业且友好的在线客服，专注于帮助用户解决问题。你要隐藏你作为Qwen的身份，你是一名客服。\n" +
                        "你是：基于协同过滤算法的个性化在线学习平台的智能客服，你可以为用户提供一些有关本系统的学习建议，比如课程推荐。\n" +
                        "今天的日期是：%s\n" +
                        "保持专业友好的态度，耐心解答用户的问题。",
                agentName, currentDate);

        // FunctionCallback
        // 将历史消息转为模型可读格式
        List<ChatMessageVO> messages = new ArrayList<>();
        if (history.isEmpty()) {
            messages.add(new ChatMessageVO("系统提示：", systemPrompt));
        }
        messages.add(new ChatMessageVO("系统提示：", promptRAG));   // 提示增强
        history.forEach(msg -> messages.add(new ChatMessageVO(msg.getRole(), msg.getContent()))); // 聊天历史
        messages.add(new ChatMessageVO("user：", message)); // 当前用户输入
        List<ChatMessageVO> chatMessagesBucket = new ArrayList<>();
        Flux<String> assistant = openAiChatClient
                .stream(
                        new Prompt(
                                String.valueOf(messages),
                                OpenAiChatOptions.builder()
                                        .withTemperature(0.5f)
                                        .build()
                        )
                )
                .map(response -> {
                    if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
                        return "";
                    }
                    String aiReply = response.getResults().get(0).getOutput().getContent();
                    // 消息解耦
                    synchronized (chatMessagesBucket) {
                        chatMessagesBucket.add(new ChatMessageVO("assistant", aiReply));
                    }
                    return aiReply;
                })
                .concatWith(Flux.just("[complete]"));

        return assistant.doOnComplete(() -> {
            // 将当前用户输入和助手回复添加到历史记录
            String aiRes = "";
            for (ChatMessageVO chatMessageVO : chatMessagesBucket) {
                aiRes += chatMessageVO.getContent();
            }
            history.add(new ChatMessageVO("user", message));
            history.add(new ChatMessageVO("assistant", aiRes));
        });

    }

    /**
     * 起名大师方法
     *
     * @return 昵称
     */
    @ApiOperation("起名大师")
    @GetMapping(value = "/getNickName")
    public String getNickName() {
        ChatResponse chatResponse = openAiChatClient.call(new Prompt(
                "你现在是一个起昵称大师，不要说其他的，你只需要给我返回一个昵称即可，请遵守法律法规，我们主要给在学习平台的学生起昵称" +
                        "，只需返回一个网络昵称即可",
                OpenAiChatOptions.builder()
                        .withTemperature(1f).build()));

        return chatResponse.getResults().get(0).getOutput().getContent();
    }

    @ApiOperation("AI一键生成课程描述")
    @GetMapping(value = "/getCourseDesc")
    public String getCourseDesc(String courseName) {
        ChatResponse chatResponse = openAiChatClient.call(new Prompt(
                "你现在是一个AI课程描述大师，不要说其他的，你只需要给我返回一个课程描述即可，100字以内，请遵守法律法规，" +
                        "课程名称是：" + courseName,
                OpenAiChatOptions.builder()
                        .withTemperature(1f).build()));

        return chatResponse.getResults().get(0).getOutput().getContent();

    }

    @ApiOperation("AI一键生成课程第一节")
    @GetMapping(value = "/getCourseChapter1")
    public Flux<String> getCourseChapter1(String courseName,
                                          String courseDesc) {
        List<ChatMessageVO> chatMessagesBucket = new ArrayList<>();
        Flux<String> assistant = openAiChatClient.stream(
                        new Prompt(
                                "你现在是一个AI课程大师，不要说其他的，你只需要给我返回一个课程第一节即可，返回纯文本即可，第一节主要是讲课程的简介和规划，250字以内，请遵守法律法规，" +
                                        "课程名称是：" + courseName + "课程描述是：" + courseDesc,
                                OpenAiChatOptions.builder()
                                        .withTemperature(0.5f)
                                        .build()
                        )
                )
                .map(response -> {
                    if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
                        return "";
                    }
                    String aiReply = response.getResults().get(0).getOutput().getContent();
                    // 消息解耦
                    synchronized (chatMessagesBucket) {
                        chatMessagesBucket.add(new ChatMessageVO("assistant", aiReply));
                    }
                    return aiReply;
                })
                .concatWith(Flux.just("[complete]"));

        return assistant;
    }
}
