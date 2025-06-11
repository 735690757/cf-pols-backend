package com.karrycode.cfpolsbackend.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.karrycode.cfpolsbackend.common.R;
import com.karrycode.cfpolsbackend.domain.dto.InteractionD;
import com.karrycode.cfpolsbackend.domain.dto.LearningProgressD;
import com.karrycode.cfpolsbackend.domain.po.Course;
import com.karrycode.cfpolsbackend.domain.po.Order;
import com.karrycode.cfpolsbackend.domain.vo.RateOrderVO;
import com.karrycode.cfpolsbackend.domain.vo.StudentCourse;
import com.karrycode.cfpolsbackend.domain.vo.SysInnerOrder;
import com.karrycode.cfpolsbackend.domain.vo.TeacherCourseVO;
import com.karrycode.cfpolsbackend.service.CourseService;
import com.karrycode.cfpolsbackend.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 订单表(Order)控制层
 *
 * @author makejava
 * @since 2025-01-11 17:46:24
 */
@CrossOrigin
@RestController
@RequestMapping("/order")
public class OrderController {
    /**
     * 服务对象
     */
    @Autowired
    private OrderService orderService;
    @Autowired
    private CourseService courseService;

    /**
     * @param interactionD 交互对象
     * @return R
     */
    @ApiOperation("购买课程，添加订单记录")
    @PostMapping("/buyCourse")
    public R buyCourse(@RequestBody InteractionD interactionD) {
        try {
            orderService.buyCourse(interactionD.getUserId(), interactionD.getCourseId());
        } catch (Exception e) {
            return R.error(e.getMessage(), 500);
        }
        return R.success("购买成功");
    }

    /**
     * @param userId   用户id
     * @param courseId 课程id
     * @return R
     */
    @ApiOperation("检查用户是否购买过课程")
    @GetMapping("/checkBuyCourse")
    public R checkBuyCourse(@RequestParam String userId, @RequestParam String courseId) {
        LambdaQueryWrapper<Order> orderLambdaQueryWrapper =
                new LambdaQueryWrapper<Order>().eq(Order::getUserId, userId).eq(Order::getCourseId, courseId);
        Order preOrder = orderService.getOne(orderLambdaQueryWrapper);
        if (preOrder != null) {
            return R.success(1);
        }
        return R.success(0);
    }

    /**
     * @param userId 用户id
     * @return R
     */
    @ApiOperation("获取用户购买过的课程")
    @GetMapping("/getBuyCourse")
    public R getBuyCourse(@RequestParam String userId) {
        List<Course> myCourseList = orderService.myCourse(userId);
        List<LearningProgressD> myCourseLearningProgress = courseService.getLearningProgress(myCourseList, Integer.parseInt(userId));
        return R.success(myCourseList).add("learningProgress", myCourseLearningProgress);
    }

    /**
     * @param rateOrderVO 课程评分值对象
     * @return R
     */
    @ApiOperation("为课程评分")
    @PostMapping("/rateCourse")
    public R rateCourse(@RequestBody RateOrderVO rateOrderVO) {
        boolean isSuccess = orderService.rateCourse(rateOrderVO);
        if (!isSuccess) {
            return R.error("评分失败");
        }
        return R.success("评分成功");
    }

    /**
     * @param rateOrderVO 课程评分值对象
     * @return R
     */
    @ApiOperation("获取课程评分")
    @PostMapping("/getCourseRate")
    public R getCourseRate(@RequestBody RateOrderVO rateOrderVO) {
        double courseRate = orderService.getCourseRate(rateOrderVO);
        return R.success(courseRate);
    }

    /**
     * 获取指定教师的全部订单收益
     *
     * @param teacherId 教师id
     * @return R
     */
    @ApiOperation("获取指定教师的全部订单收益")
    @GetMapping("/getTeacherEarning")
    public R getTeacherEarning(@RequestParam Integer teacherId) {
        return R.success(orderService.getTeacherEarning(teacherId));
    }

    /**
     * 获取指定教师的订单数量，附带浏览量
     *
     * @param teacherId 教师id
     * @return R
     */
    @ApiOperation("获取指定教师的订单数量，附带浏览量")
    @GetMapping("/getTeacherOrderCount")
    public R getTeacherOrderCount(@RequestParam Integer teacherId) {
        List<Course> courseList = courseService.lambdaQuery()
                .eq(Course::getTeacherId, teacherId).list();
        List<Integer> courseIDList = courseList.stream().map(Course::getId).toList();
        if (courseIDList.isEmpty()) {
            // 获取全部浏览量
            int allViewCount = 0;
            for (Course course : courseService.list()) {
                allViewCount += course.getViewCount();
            }
            // 获取全部订单总量
            Long allOrderCount = orderService.lambdaQuery().count();
            double allOrderLearningPoint = 0;
            double earning = 0;

            for (Order order : orderService.list()) {
                allOrderLearningPoint += Double.parseDouble(order.getAmount());
            }
            return R.success(0)
                    .add("viewCount", 0)
                    .add("allViewCount", allViewCount)
                    .add("allOrderCount", allOrderCount)
                    .add("allOrderLearningPoint", allOrderLearningPoint)
                    .add("earning", earning);
        }
        Long count = orderService.lambdaQuery().in(Order::getCourseId, courseIDList).count();
        int viewCount = 0;
        for (Course course : courseList) {
            viewCount += course.getViewCount();
        }
        // 获取全部浏览量
        int allViewCount = 0;
        for (Course course : courseService.list()) {
            allViewCount += course.getViewCount();
        }
        // 获取全部订单总量
        Long allOrderCount = orderService.lambdaQuery().count();
        // 获取全部订单学习点量
        double allOrderLearningPoint = 0;
        double earning = 0;

        for (Order order : orderService.list()) {
            allOrderLearningPoint += Double.parseDouble(order.getAmount());
        }
        // 教师订单收益
        for (Order order : orderService.lambdaQuery().in(Order::getCourseId, courseIDList).list()) {
            earning += Double.parseDouble(order.getAmount());
        }

        return R.success(count)
                .add("viewCount", viewCount)
                .add("allViewCount", allViewCount)
                .add("allOrderCount", allOrderCount)
                .add("allOrderLearningPoint", allOrderLearningPoint)
                .add("earning", earning);
    }

    /**
     * 给定uid，返回已购买课程（管理员）
     *
     * @param uid 用户ID
     * @return R
     */
    @ApiOperation("给定uid，返回已购买课程（管理员）")
    @GetMapping("/getBuyCourseByUid")
    public R getBuyCourseByUid(@RequestParam Integer uid) {
        List<StudentCourse> studentCourseList = orderService.listStudentCourse(uid);
        return R.success(studentCourseList);
    }

    /**
     * 给定uid，返回教师课程（管理员）
     *
     * @param uid 用户ID
     * @return R
     */
    @ApiOperation("给定uid，返回教师课程（管理员）")
    @GetMapping("/getTeacherCourseByUid")
    public R getTeacherCourseByUid(@RequestParam Integer uid) {
        List<TeacherCourseVO> teacherCourseVOList = orderService.listTeacherCourse(uid);
        return R.success(teacherCourseVOList);
    }

    @ApiOperation("获取系统订单")
    @GetMapping("/getSystemOrder")
    public R getSystemOrder() {
        List<SysInnerOrder> orderList = orderService.listUpper();
        Integer nowDayCount = courseService.getNowDayCount();
        Double allEarning = orderService.getAllDayEarning();
        return R.success(orderList).add("nowDayCount", nowDayCount)
                .add("allEarning", allEarning);
    }
}
