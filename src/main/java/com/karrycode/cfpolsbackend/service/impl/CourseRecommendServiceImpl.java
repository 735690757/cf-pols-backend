package com.karrycode.cfpolsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.karrycode.cfpolsbackend.domain.dto.AdminRecommendedD;
import com.karrycode.cfpolsbackend.domain.po.Course;
import com.karrycode.cfpolsbackend.domain.po.Order;
import com.karrycode.cfpolsbackend.domain.vo.UccVO;
import com.karrycode.cfpolsbackend.service.CourseRecommendService;
import com.karrycode.cfpolsbackend.service.CourseService;
import com.karrycode.cfpolsbackend.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/4/9 09:30
 * @PackageName com.karrycode.cfpolsbackend.service.impl
 * @ClassName CourseRecommendServiceImpl
 * @Description
 * @Version 1.0
 */
@Service
public class CourseRecommendServiceImpl implements CourseRecommendService {
    @Resource
    private OrderService orderService;
    @Resource
    private CourseService courseService;
    @Resource
    private MongoTemplate mongoTemplate;
    //  用户评分向量 for 管理员
    static Map<Integer, Map<Integer, Double>> userRatings = new HashMap<>();

    /**
     * 计算标准差
     *
     * @param ratings 评分
     * @return 标准差
     */
    double standardDeviationCalculations(double[] ratings) {
        double std = 0;        // 标准差
        double avarage = 0;    // 平均值
        double sum = 0;           // 评分和
        double varianceUP = 0; // 方差分子
        double variance = 0; // 方差
        for (int i = 0; i < ratings.length; i++) {
            sum += ratings[i];
        }
        avarage = sum / ratings.length;
        for (int i = 0; i < ratings.length; i++) { // 方差分子
            varianceUP += Math.pow(ratings[i] - avarage, 2);
        }
        variance = varianceUP / (ratings.length - 1);
        if (ratings.length == 1) {
            return 0;
        }
        std = Math.sqrt(variance);
        return std;
    }

    /**
     * 计算协方差
     *
     * @param X 平均值 X
     * @param Y 平均值 Y
     * @return 协方差
     */

    double covarianceCalculation(double[] X, double[] Y) {
        double covariance = 0;       // 协方差
        double avarageX = 0;         // 平均值 X
        double avarageY = 0;         // 平均值 Y
        if (X.length == 1 || Y.length == 1) {
            return 0;
        }
        for (int i = 0; i < X.length; i++) {
            avarageX += X[i];
        }
        avarageX = avarageX / X.length;
        for (int i = 0; i < Y.length; i++) {
            avarageY += Y[i];
        }
        avarageY = avarageY / Y.length;
        for (int i = 0; i < X.length; i++) {
            covariance += (X[i] - avarageX) * (Y[i] - avarageY);
        }
        covariance = covariance / (X.length - 1);
//        System.out.println("X:"+ Arrays.toString(X) +"Y:"+ Arrays.toString(Y));
        return covariance;
    }

    /**
     * 计算皮尔逊相关系数
     *
     * @param covarianceXY 协方差
     * @param stdX         标准差 X
     * @param stdY         标准差 Y
     * @return 相关系数
     */
    double pearsonCorrelationCoefficient(double covarianceXY, double stdX, double stdY) {
        return covarianceXY / (stdX * stdY);
    }

    /**
     * 协同过滤
     *
     * @param userId 用户ID
     * @return 推荐课程列表
     */
    @Override
    public List<Course> getRecommendCourse(Integer userId) {
        // 获取用户学过的课程和评分
        LambdaQueryWrapper<Order> orderLambdaQueryWrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId);
        List<Order> orders = orderService.list(orderLambdaQueryWrapper);
        // 从order中解耦出cid列表
        List<Integer> cidList = orders.stream().map(Order::getCourseId).toList();
        if (cidList.isEmpty()) {
            // 返回空，可以推荐热门课程,或者推广课程
            Course popularCourse = courseService.getTheMostViewCount();
            return List.of(popularCourse);
        } else if (cidList.size() == 1) {
            // 半冷启动，特殊推荐课程，主要推荐和这个用户打相近分数的，并且给其他课程打高分的
            int nowScore;
            try {
                nowScore = (int) Double.parseDouble(orders.get(0).getScore());
            } catch (Exception e) {
                // 没打分，取平均分
                double avg = orderService.lambdaQuery().eq(Order::getCourseId, cidList.get(0)).notIn(Order::getUserId, userId)
                        .list().stream().map(Order::getScore).mapToDouble(Double::parseDouble).average().orElse(0);
                nowScore = (int) avg;
            }
            LambdaQueryWrapper<Order> orderLqwTS;
            if (nowScore != 5) {
                orderLqwTS = new LambdaQueryWrapper<Order>()
                        .in(Order::getScore, Arrays.asList(nowScore, nowScore + 1))
                        .eq(Order::getCourseId, cidList.get(0))
                        .notIn(Order::getUserId, userId);
            } else {
                orderLqwTS = new LambdaQueryWrapper<Order>()
                        .in(Order::getScore, Arrays.asList(nowScore, nowScore - 1))
                        .eq(Order::getCourseId, cidList.get(0))
                        .notIn(Order::getUserId, userId);
            }
            List<Order> ordersTS = orderService.list(orderLqwTS);
            List<Integer> otherUidList = ordersTS.stream().map(Order::getUserId).toList();
            if (otherUidList.isEmpty()) {
                Course popularCourse = courseService.getTheMostViewCount();
                return List.of(popularCourse);
            }
            System.out.println("otherUidList = " + otherUidList);
            LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<Order>()
                    .in(Order::getUserId, otherUidList)
                    .in(Order::getScore, Arrays.asList("4", "5"))
                    .notIn(Order::getCourseId, cidList.get(0))
                    .groupBy(Order::getCourseId)
                    .orderByDesc(Order::getScore);
            List<Order> list = orderService.list(queryWrapper);
            List<Integer> cidAnsList = list.stream().map(Order::getCourseId).toList();
            System.out.println("cidAnsList = " + cidAnsList);
            LambdaQueryWrapper<Course> queryWrapperAns = new LambdaQueryWrapper<Course>()
                    .in(Course::getId, cidAnsList);
            return courseService.list(queryWrapperAns);
        }
        // 根据cid列表查询课程
        LambdaQueryWrapper<Order> otherUserOrderLambdaQueryWrapper = new LambdaQueryWrapper<Order>()
                .in(Order::getCourseId, cidList);
        List<Order> otherUserOrders = orderService.list(otherUserOrderLambdaQueryWrapper);
//        System.out.println(otherUserOrders);
        // 从otherUserOrders中解耦出二维矩阵【课程id，用户id】，矩阵中值为课程评分
        // 评分矩阵 Map<courseId, Map<userId, score>>
        Map<Integer, Map<Integer, Double>> ratingMatrix = new HashMap<>();

        for (Order order : otherUserOrders) {
            Integer courseId = order.getCourseId();
            Integer userIdNow = order.getUserId();
            Double score = null;
            System.out.println("order.getScore() = " + order.getScore());

            // 尝试将评分从字符串转换为 Double
            try {
                score = Double.parseDouble(order.getScore());
            } catch (Exception e) {
                continue; // 忽略非法评分
            }

            // 如果没有该课程，则新建内层 map
            ratingMatrix
                    .computeIfAbsent(courseId, k -> new HashMap<>())
                    .put(userIdNow, score);
        }
        System.out.println("ratingMatrix--------------------");
        System.out.println(ratingMatrix);
        System.out.println("--------------------------------");
        // 1. 收集所有用户和课程的 ID
        Set<Integer> userIdSet = new HashSet<>();
        Set<Integer> courseIdSet = new HashSet<>();
        for (Order order : otherUserOrders) {
            userIdSet.add(order.getUserId());
            courseIdSet.add(order.getCourseId());
        }

        // 2. 排序并建立索引映射
        List<Integer> userIdList = new ArrayList<>(userIdSet);
        List<Integer> courseIdList = new ArrayList<>(courseIdSet);
        Collections.sort(userIdList); // 确保顺序一致
        Collections.sort(courseIdList);

        Map<Integer, Integer> userIdToIndex = new HashMap<>();
        Map<Integer, Integer> courseIdToIndex = new HashMap<>();

        for (int i = 0; i < userIdList.size(); i++) {
            userIdToIndex.put(userIdList.get(i), i);
        }
        for (int j = 0; j < courseIdList.size(); j++) {
            courseIdToIndex.put(courseIdList.get(j), j);
        }

        // 3. 创建评分矩阵（行是课程，列是用户）
        int courseCount = courseIdList.size();
        int userCount = userIdList.size();
        double[][] ratingMatrixFit = new double[courseCount][userCount]; // 默认值为0

        for (Order order : otherUserOrders) {
            Integer userIdNow = order.getUserId();
            Integer courseId = order.getCourseId();
            try {
                double score = Double.parseDouble(order.getScore());
                int courseIndex = courseIdToIndex.get(courseId); // 行
                int userIndex = userIdToIndex.get(userIdNow);    // 列
                ratingMatrixFit[courseIndex][userIndex] = score;
            } catch (Exception e) {
                // 忽略非法评分
            }
        }
        System.out.println("userIdList----" + userIdList);
        System.out.println("courseIdList----" + courseIdList);
        // 从数据库获取courseIdList课程的平均值，赋值给ratingMatrixFitAVG
        double[][] ratingMatrixFitAVG = new double[courseCount][userCount];
        for (int i = 0; i < courseCount; i++) {
            double sum = 0;
            int count = 0;
            for (int j = 0; j < userCount; j++) {
                if (ratingMatrixFit[i][j] != 0) {
                    sum += ratingMatrixFit[i][j];
                    count++;
                }
            }
            if (count > 0) {
                double avg = sum / count;
                for (int j = 0; j < userCount; j++) {
                    ratingMatrixFitAVG[i][j] = avg;
                }
                System.out.println("课程id：" + courseIdList.get(i) + " 平均值：" + avg);
            }
        }
        // 对应填充ratingMatrixFit中为0的元素
        for (int i = 0; i < courseCount; i++) {
            for (int j = 0; j < userCount; j++) {
                if (ratingMatrixFit[i][j] == 0.0) {
                    ratingMatrixFit[i][j] = Math.round(ratingMatrixFitAVG[i][j] * 10) / 10.0;
                }
            }
        }
        // ratingMatrixFitAVG转置
        double[][] ratingMatrixFitT = new double[userCount][courseCount];
        for (int i = 0; i < ratingMatrixFit.length; i++) {
            for (int j = 0; j < ratingMatrixFit[i].length; j++) {
                ratingMatrixFitT[j][i] = ratingMatrixFit[i][j];
            }
        }
        System.out.println("ratingMatrixFitT--------------------");

        for (int i = 0; i < ratingMatrixFitT.length; i++) {
            for (int j = 0; j < ratingMatrixFitT[i].length; j++) {
                System.out.print(ratingMatrixFitT[i][j] + " ");
            }
            System.out.print(" \n");
        }

        System.out.println("--------------------------------");
        // 从userIdList获取目标用户userid的键
        int targetUserIndex = userIdToIndex.get(userId);
        System.out.println("目标用户i:" + targetUserIndex);


        //--------------------- 计算标准差 -----------------
        ArrayList<Double> stdVector = new ArrayList<>();    // 方差向量
        for (int i = 0; i < ratingMatrixFitT.length; i++) {
            stdVector.add(standardDeviationCalculations(ratingMatrixFitT[i]));
        }
        System.out.println("标准差向量：" + stdVector);

        //---------------- 计算协方差 cov (X,Y)------------
        ArrayList<Double> covarianceVector = new ArrayList<>(); // 协方差向量
        for (int i = 0; i < ratingMatrixFitT.length; i++) {
            covarianceVector.add(covarianceCalculation(ratingMatrixFitT[targetUserIndex], ratingMatrixFitT[i]));
        }
        System.out.println("协方差向量：" + covarianceVector);

        //-------------- 皮尔逊相关系数 ----------------
        ArrayList<Double> pearsonVector = new ArrayList<>();    // 皮尔逊相关系数向量
        for (int i = 0; i < ratingMatrixFitT.length; i++) {
            pearsonVector.add(pearsonCorrelationCoefficient(covarianceVector.get(i), stdVector.get(targetUserIndex), stdVector.get(i)));
        }
        // 将NaN用0.0来替换
        for (int i = 0; i < pearsonVector.size(); i++) {
            if (Double.isNaN(pearsonVector.get(i))) {
                pearsonVector.set(i, 0.0);
            }
        }
        System.out.println("皮尔逊相关系数向量：" + pearsonVector);
        // 获取最大值与次大值所对应的索引,并且索引不能是targetUserIndex
        int maxIndex = -1;
        int secondMaxIndex = -1;
        double maxValue = Double.NEGATIVE_INFINITY;
        double secondMaxValue = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < pearsonVector.size(); i++) {
            if (i == targetUserIndex) continue;
            double value = pearsonVector.get(i);
            if (value > maxValue) {
                secondMaxValue = maxValue;
                secondMaxIndex = maxIndex;
                maxValue = value;
                maxIndex = i;
            } else if (value > secondMaxValue) {
                secondMaxValue = value;
                secondMaxIndex = i;
            }
        }
        System.out.println("最大值索引：" + maxIndex);
        System.out.println("次大值索引：" + secondMaxIndex);
        if (maxIndex == -1 || secondMaxIndex == -1) {
            System.out.println("没有找到满足条件的用户");
            return List.of();
        }
        // maxIndex及secondMaxIndex获取uid
        Integer maxUid = userIdList.get(maxIndex);
        Integer secondMaxUid = userIdList.get(secondMaxIndex);
        System.out.println("最大值uid：" + maxUid);
        System.out.println("次大值uid：" + secondMaxUid);
        // 从数据库获取maxUid和secondMaxUid对应的课程id，但是不包括cidList
        LambdaQueryWrapper<Order> select = new LambdaQueryWrapper<Order>()
                .notIn(Order::getCourseId, cidList)
                .and(wrapper -> wrapper
                        .eq(Order::getUserId, maxUid)
                        .or()
                        .eq(Order::getUserId, secondMaxUid)
                ).and(wrapper -> wrapper
                        .eq(Order::getScore, "4")
                        .or()
                        .eq(Order::getScore, "5")
                )
                .groupBy(Order::getCourseId)
                .orderByDesc(Order::getScore)
                .select(Order::getCourseId);
        List<Integer> recommendCourseIdList = orderService.listObjs(select)
                .stream()
                .map(courseId -> (Integer) courseId)  // 将 Object 转换为 Integer
                .collect(Collectors.toList());
        System.out.println(recommendCourseIdList);
        if (recommendCourseIdList.isEmpty()) {
            // 如果推荐课程为空，则返回一个空
            return List.of();
        }
        LambdaQueryWrapper<Course> recommendCourse = new LambdaQueryWrapper<Course>()
                .in(Course::getId, recommendCourseIdList);
        return courseService.list(recommendCourse);
        // 释放资源
    }

    /**
     * 管理员竞价推荐
     *
     * @param recommendCourse 原始推荐课程
     * @param userId          用户id
     * @return 最终推荐课程
     */
    @Override
    public List<Course> recommendWithAdmin(List<Course> recommendCourse, Integer userId) {
        if (recommendCourse.size() >= 3) {
            return recommendCourse;
        }
        // 否则就要查询admin所推荐的课程推荐课程
        List<AdminRecommendedD> allRecommended = mongoTemplate.findAll(AdminRecommendedD.class);
        List<Integer> allCourseID = allRecommended.stream().map(AdminRecommendedD::getCourseId).toList();
        if (allCourseID.isEmpty()) {
            return recommendCourse;
        }
        List<Integer> userLearnCourseID = orderService.lambdaQuery()
                .eq(Order::getUserId, userId).list().stream().map(Order::getCourseId).toList();
        List<Course> courseList;
        if (userLearnCourseID.isEmpty()) {
            courseList = courseService.lambdaQuery().in(Course::getId, allCourseID)
                    .and(wrapper -> wrapper.eq(Course::getStatus, 1))
                    .list();
        } else {
            courseList = courseService.lambdaQuery().in(Course::getId, allCourseID)
                    .and(wrapper -> wrapper.eq(Course::getStatus, 1))
                    .and(wrapper -> wrapper.notIn(Course::getId, userLearnCourseID))
                    .list();
        }
        if (courseList.isEmpty()) {
            return recommendCourse;
        }
        List<Course> result = new ArrayList<>(recommendCourse);
        Collections.shuffle(courseList);
        result.addAll(courseList);
        return result;
    }

    /**
     * 根据浏览量排序
     *
     * @param dedupedList 去重后的课程列表
     * @param userId      用户ID
     * @return 排序后的课程列表
     */
    @Override
    public List<Course> sortByUserViewCount(List<Course> dedupedList, Integer userId) {
        List<Integer> courseIDList = dedupedList.stream().map(Course::getId).toList();
        return courseService.sortByUserViewCount(courseIDList, userId);
    }


    // 添加评分记录
    public void add(int userId, int courseId, double score) {
        userRatings.computeIfAbsent(userId, k -> new HashMap<>()).put(courseId, score);
    }

    // 余弦相似度计算
    public double cosineSimilarity(double[] v1, double[] v2) {
        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (int i = 0; i < v1.length; i++) {
            dot += v1[i] * v2[i];
            norm1 += v1[i] * v1[i];
            norm2 += v2[i] * v2[i];
        }
        if (norm1 == 0 || norm2 == 0) return 0.0;
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    public HashMap<String, Object> CovAdminEE() {
        // 清空map
        userRatings.clear();
        List<UccVO> uccVOS = orderService.listUcc();
        for (UccVO uccVO : uccVOS) {
            add(uccVO.getUserId(), uccVO.getCourseId(), uccVO.getScore());
        }
        // 获取用户和课程，创建用户和课程的映射，这里顺序不变
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

        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("similarityMatrix", similarityMatrix);
        stringObjectHashMap.put("users", users);
        return stringObjectHashMap;

    }


}
