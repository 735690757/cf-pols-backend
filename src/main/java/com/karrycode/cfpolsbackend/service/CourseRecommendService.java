package com.karrycode.cfpolsbackend.service;

import com.karrycode.cfpolsbackend.domain.po.Course;

import java.util.HashMap;
import java.util.List;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/4/9 09:29
 * @PackageName com.karrycode.cfpolsbackend.service
 * @ClassName CourseRecommendService
 * @Description
 * @Version 1.0
 */
public interface CourseRecommendService {
    /**
     * 协同过滤
     *
     * @param userId 用户ID
     * @return 推荐课程列表
     */
    List<Course> getRecommendCourse(Integer userId);

    /**
     * 管理员竞价推荐
     *
     * @param recommendCourse 原始推荐课程
     * @param userId          用户ID
     * @return 最终推荐课程
     *
     */
    List<Course> recommendWithAdmin(List<Course> recommendCourse, Integer userId);

    /**
     * 重排序
     *
     * @param dedupedList 去重后的课程列表
     * @param userId 用户ID
     * @return 重排序后的课程列表
     */
    List<Course> sortByUserViewCount(List<Course> dedupedList, Integer userId);

    /**
     * 添加向量
     *
     * @param userId 用户ID
     * @param courseId 课程ID
     * @param score 评分
     */
    void add(int userId, int courseId, double score);

    /**
     * 余弦相似度
     *
     * @param v1 v1
     * @param v2 v2
     * @return 相似度
     */
    double cosineSimilarity(double[] v1, double[] v2);

    /**
     * 二维热力矩阵
     *
     * @return 热力矩阵
     */
    HashMap<String, Object> CovAdminEE();

}
