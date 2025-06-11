package com.karrycode.cfpolsbackend.service.impl;

import com.karrycode.cfpolsbackend.CfPolsBackendApplicationTests;
import com.karrycode.cfpolsbackend.domain.vo.UccVO;
import com.karrycode.cfpolsbackend.service.OrderService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;

import java.util.*;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/4/9 09:33
 * @PackageName com.karrycode.cfpolsbackend.service.impl
 * @ClassName CourseRecommendServiceImplTest
 * @Description
 * @Version 1.0
 */
class CourseRecommendServiceImplTest extends CfPolsBackendApplicationTests {

    @Resource
    OrderService orderService;

    static Map<Integer, Map<Integer, Double>> userRatings = new HashMap<>();

    // 添加评分记录
    static void add(int userId, int courseId, double score) {
        userRatings.computeIfAbsent(userId, k -> new HashMap<>()).put(courseId, score);
    }

    // 余弦相似度计算
    static double cosineSimilarity(double[] v1, double[] v2) {
        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (int i = 0; i < v1.length; i++) {
            dot += v1[i] * v2[i];
            norm1 += v1[i] * v1[i];
            norm2 += v2[i] * v2[i];
        }
        if (norm1 == 0 || norm2 == 0) return 0.0;
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    @Test
    public void testRecommendCourse() {
        List<UccVO> uccVOS = orderService.listUcc();
        for (UccVO uccVO : uccVOS) {
            add(uccVO.getUserId(), uccVO.getCourseId(), uccVO.getScore());
        }
        // 获取用户和课程全集
        List<Integer> users = new ArrayList<>(userRatings.keySet());
        Set<Integer> allCourses = new HashSet<>();
        for (Map<Integer, Double> ratings : userRatings.values()) {
            allCourses.addAll(ratings.keySet());
        }
        List<Integer> courses = new ArrayList<>(allCourses);

        // 构建用户评分向量
        Map<Integer, double[]> userVectors = new HashMap<>();
        for (Integer user : users) {
            double[] vector = new double[courses.size()];
            Map<Integer, Double> ratings = userRatings.get(user);
            for (int i = 0; i < courses.size(); i++) {
                vector[i] = ratings.getOrDefault(courses.get(i), 0.0);
            }
            userVectors.put(user, vector);
        }

        // 打印相似度矩阵标题
        System.out.printf("%8s", "用户");
        for (Integer u : users) {
            System.out.printf("%8d", u);
        }
        System.out.println();

        int n = users.size();
        double[][] similarityMatrix = new double[n][n]; // 初始化二维数组

        // 计算相似度并存入矩阵
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Integer u1 = users.get(i);
                Integer u2 = users.get(j);
                double sim = cosineSimilarity(userVectors.get(u1), userVectors.get(u2));
                similarityMatrix[i][j] = sim;
            }
        }

        for (int i = 0; i < n; i++) {
            System.out.printf("%8d", users.get(i));
            for (int j = 0; j < n; j++) {
                System.out.printf("%8.4f", similarityMatrix[i][j]);
            }
            System.out.println();
        }

    }

}