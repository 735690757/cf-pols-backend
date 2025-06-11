package com.karrycode.cfpolsbackend.service.impl;

import com.karrycode.cfpolsbackend.domain.po.Course;
import com.karrycode.cfpolsbackend.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/2/10 10:38
 * @PackageName com.karrycode.cfpolsbackend.service.impl
 * @ClassName KnowledgeBaseService
 * @Description
 * @Version 1.0
 */
@Service
public class KnowledgeBaseService {
    @Autowired
    private CourseService courseService;

    // 模拟知识库
    private final Map<String, Supplier<String>> knowledgeBase = Map.of(
            "协同过滤", () -> "协同过滤算法是一种基于用户行为或物品相似度的推荐算法...",
            "机器学习", () -> "机器学习是一种让计算机通过数据进行自我学习的技术...",
            "推荐系统", () -> "推荐系统是一种利用大数据和机器学习算法...",
            "刘珂瑞", () -> "刘珂瑞是一名北华大学大四毕业生，他现在正在给老师们演示他的毕业设计，他的毕设名称是《基于协同过滤算法的个性化在线学习系统》",
            "瑞鸭", () -> "瑞鸭是刘珂瑞自己起的一个名字，广泛用于他的各种课程设计之中",
            "最受欢迎的课程", () -> {
                Course mostPop = courseService.getTheMostViewCount();
                return "当前最受欢迎、热度最高的课程是《" + mostPop.getTitle() +
                        "》，链接是：http://localhost:5173/student/courseDetail?id=" + mostPop.getId()+"(system：输出链接后要换行)";
            }
    );

    /**
     * 模拟知识库检索
     *
     * @param query 查询
     * @return 提示词
     */
    public String retrieveRelevantKnowledge(String query) {
        // 简化版关键词匹配
        return knowledgeBase.entrySet().stream()
                .filter(entry -> query.contains(entry.getKey()))
                .map(entry -> entry.getValue().get())  // 调用 Supplier#get
                .findFirst()
                .orElse("");
    }
}
