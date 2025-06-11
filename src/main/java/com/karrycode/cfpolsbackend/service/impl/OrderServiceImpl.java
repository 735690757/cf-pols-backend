package com.karrycode.cfpolsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karrycode.cfpolsbackend.domain.po.Course;
import com.karrycode.cfpolsbackend.domain.po.Order;
import com.karrycode.cfpolsbackend.domain.po.User;
import com.karrycode.cfpolsbackend.domain.vo.*;
import com.karrycode.cfpolsbackend.mapper.OrderMapper;
import com.karrycode.cfpolsbackend.service.CourseService;
import com.karrycode.cfpolsbackend.service.OrderService;
import com.karrycode.cfpolsbackend.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单表(Order)表服务实现类
 *
 * @author makejava
 * @since 2025-01-11 17:46:24
 */
@Service

public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Resource
    private CourseService courseService;
    @Resource
    private UserService userService;

    /**
     * 购买课程
     *
     * @param userId   用户id
     * @param courseId 课程id
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean buyCourse(String userId, String courseId) {
        // 检查用户是否已经购买过该课程
        LambdaQueryWrapper<Order> orderLambdaQueryWrapper =
                new LambdaQueryWrapper<Order>().eq(Order::getUserId, userId).eq(Order::getCourseId, courseId);
        Order preOrder = getOne(orderLambdaQueryWrapper);
        if (preOrder != null) {
            throw new RuntimeException("已经购买过该课程");
        }
        // 获取当前课程
        LambdaQueryWrapper<Course> courseLambdaQueryWrapper =
                new LambdaQueryWrapper<Course>().eq(Course::getId, courseId);
        Course nowCourse = courseService.getOne(courseLambdaQueryWrapper);
        // 获取用户信息
        LambdaQueryWrapper<User> userLambdaQueryWrapper =
                new LambdaQueryWrapper<User>().eq(User::getId, userId);
        User nowUser = userService.getOne(userLambdaQueryWrapper);
        if (nowCourse == null || nowUser == null) {
            throw new RuntimeException("课程或用户不存在");
        }
        // 高级会员检查
        boolean isAdvanced = nowUser.getFund().equals("-1");
        // 普通用户购买课程，并且余额不足
        if (!isAdvanced && Double.parseDouble(nowUser.getFund()) < Double.parseDouble(nowCourse.getPrice())) {
            throw new RuntimeException("余额不足");
        } else {
            // 普通用户扣除余额
            nowUser.setFund(String.valueOf(Double.parseDouble(nowUser.getFund()) - Double.parseDouble(nowCourse.getPrice())));
            userService.updateById(nowUser);
        }
        // 创建订单
        Order nowOrder = Order.builder()
                .courseId(Integer.valueOf(courseId))
                .userId(Integer.valueOf(userId))
                .amount(isAdvanced ? "0" : nowCourse.getPrice())
                .build();
        // 保存订单
        if (this.save(nowOrder)) {
            // 课程购买数加1
            nowCourse.setBuyCount(nowCourse.getBuyCount() + 1);
            courseService.updateById(nowCourse);
            // 给教师增加收入
            Integer teacherId = nowCourse.getTeacherId();
            User teacher = userService.getById(teacherId);
            teacher.setFund(String.valueOf(Double.parseDouble(teacher.getFund()) + Double.parseDouble(nowCourse.getPrice())));
            userService.updateById(teacher);
            return true;
        }
        return false;
    }

    /**
     * 我的课程
     *
     * @param userId 用户ID
     * @return 课程列表
     */
    @Override
    public List<Course> myCourse(String userId) {
        LambdaQueryWrapper<Order> orderLambdaQueryWrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime);
        List<Order> orders = this.list(orderLambdaQueryWrapper);
        // 从order中解耦出cid列表
        List<Integer> cidList = orders.stream().map(Order::getCourseId).toList();
        // 根据cid列表查询课程
        if (!cidList.isEmpty()) {
            LambdaQueryWrapper<Course> courseLambdaQueryWrapper = new LambdaQueryWrapper<Course>()
                    .in(Course::getId, cidList);
            return courseService.list(courseLambdaQueryWrapper);
        }
        return List.of();
    }

    /**
     * 为课程评分
     *
     * @param rateOrderVO 课程评分信息
     * @return 是否评分成功
     */
    @Override
    public boolean rateCourse(RateOrderVO rateOrderVO) {
        LambdaQueryWrapper<Order> orderLambdaQueryWrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, rateOrderVO.getUserId())
                .eq(Order::getCourseId, rateOrderVO.getCourseId());
        Order order = this.getOne(orderLambdaQueryWrapper);
        if (order != null) {
            order.setScore(String.valueOf(rateOrderVO.getRate()));
            return this.updateById(order);
        }
        return false;
    }

    /**
     * 获取课程频分
     *
     * @param rateOrderVO 课程评分信息
     * @return 评分
     */
    @Override
    public double getCourseRate(RateOrderVO rateOrderVO) {
        LambdaQueryWrapper<Order> orderLambdaQueryWrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, rateOrderVO.getUserId())
                .eq(Order::getCourseId, rateOrderVO.getCourseId());
        Order order = this.getOne(orderLambdaQueryWrapper);
        if (order != null) {
            return Double.parseDouble(order.getScore());
        } else {
            return -1;
        }
    }

    @Override
    public List<CourseOrderVO> getTeacherEarning(Integer teacherId) {
        return getBaseMapper().getTeacherEarning(teacherId);
    }

    @Override
    public List<OrderCumulativeVO> listCourseTrend(Integer courseId) {
        return baseMapper.listCourseTrend(courseId);
    }

    @Override
    public List<StudentCourse> listStudentCourse(Integer uid) {
        return baseMapper.listStudentCourse(uid);
    }

    @Override
    public List<TeacherCourseVO> listTeacherCourse(Integer uid) {
        return baseMapper.listTeacherCourse(uid);
    }

    @Override
    public List<SysInnerOrder> listUpper() {
        return baseMapper.listSysOrderUpper();
    }

    @Override
    public Double getAllDayEarning() {
        return baseMapper.getAmount();
    }

    @Override
    public double getAcclaimCount(Integer courseId) {
        return baseMapper.getAcclaimCount(courseId);
    }

    @Override
    public List<UccVO> listUcc() {
        return baseMapper.listUcc();
    }
}
