package com.karrycode.cfpolsbackend.controller;

import com.karrycode.cfpolsbackend.common.R;
import com.karrycode.cfpolsbackend.domain.dto.LearningProgressD;
import com.karrycode.cfpolsbackend.domain.po.*;
import com.karrycode.cfpolsbackend.domain.vo.OrderCumulativeVO;
import com.karrycode.cfpolsbackend.domain.vo.ScoreLevelVO;
import com.karrycode.cfpolsbackend.service.*;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/4/20 11:41
 * @PackageName com.karrycode.cfpolsbackend.controller
 * @ClassName DataVisualController
 * @Description
 * @Version 1.0
 */

@CrossOrigin
@RestController
@RequestMapping("/dataVisual")
public class DataVisualController {
    @Resource
    private UserService userService;
    @Resource
    private OrderService orderService;
    @Resource
    private OrderAliService orderAliService;
    @Resource
    private PayoutService payoutService;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private CourseService courseService;

    @ApiOperation("管理员数据可视化")
    @GetMapping("/adminDataShow")
    public R adminDataShow() {
        List<User> userList = userService.list();
        List<Order> orderList = orderService.list();
        double orderFund = 0;
        for (Order order : orderList) {
            orderFund += Double.parseDouble(order.getAmount());
        }
        List<OrderAli> orderAliList = orderAliService.list();
        long pendingPayouts = payoutService.lambdaQuery().eq(Payout::getIspaid, 0).count();
        // 求getIspaid为1时的payout总钱币数
        List<String> payoutList = payoutService.lambdaQuery().ge(Payout::getIspaid, 1)
                .list().stream().map(Payout::getPayout).toList();
        double payoutFund = payoutList.stream().mapToDouble(Double::parseDouble).sum();
        // 用mongoTemplate下的learningProgress中percentage字段的平均值
        Query query = new Query();
        List<LearningProgressD> learningProgress =
                mongoTemplate.find(query, LearningProgressD.class, "learningProgress");
        double averagePercentage = learningProgress.stream().mapToDouble(LearningProgressD::getPercent).average().orElse(0);
        averagePercentage = Math.round(averagePercentage * 100) / 100.0;
        List<Course> courseList = courseService.list();

        return R.success("success")
                .add("userCount", userList.size())
                .add("orderCount", orderList.size())
                .add("orderFund", orderFund)
                .add("orderAliCount", orderAliList.size())
                .add("orderAliFund", orderAliList.stream().mapToDouble(OrderAli::getMoney).sum())
                .add("pendingPayouts", pendingPayouts)
                .add("averagePercentage", averagePercentage)
                .add("courseCount", courseList.size())
                .add("payoutFund", payoutFund);
    }

    @ApiOperation("获取指定课程的分数扇形图")
    @GetMapping("/getCourseScore")
    public R getCourseScore(Integer courseId) {
        ScoreLevelVO scoreLevelVOList = courseService.getCourseScore(courseId);
        return R.success(scoreLevelVOList);
    }

    @ApiOperation("获取全部课程的分数扇形图")
    @GetMapping("/getAllCourseScore")
    public R getAllCourseScore() {
        ScoreLevelVO scoreLevelVOList = courseService.getAllCourseScore();
        return R.success(scoreLevelVOList);
    }

    @ApiOperation("获取指定课程收益趋势")
    @GetMapping("/getCourseFundTrend")
    public R getCourseFundTrend(Integer courseId) {
        List<OrderCumulativeVO> trendList =  orderService.listCourseTrend(courseId);
        return R.success(trendList);
    }
}
