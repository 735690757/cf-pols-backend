package com.karrycode.cfpolsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.karrycode.cfpolsbackend.domain.po.Course;
import com.karrycode.cfpolsbackend.domain.po.Order;
import com.karrycode.cfpolsbackend.domain.vo.*;

import java.util.List;

/**
 * 订单表(Order)表服务接口
 *
 * @author makejava
 * @since 2025-01-11 17:46:24
 */
public interface OrderService extends IService<Order> {
    /**
     * 购买课程
     *
     * @param userId   用户id
     * @param courseId 课程id
     * @return 是否成功
     */
    boolean buyCourse(String userId, String courseId);

    /**
     * 我的课程
     *
     * @param userId 用户ID
     * @return 课程列表
     */
    List<Course> myCourse(String userId);

    /**
     * 为课程评分
     *
     * @param rateOrderVO 课程评分信息
     * @return 是否评分成功
     */
    boolean rateCourse(RateOrderVO rateOrderVO);

    /**
     * 获取课程频分
     *
     * @param rateOrderVO 课程评分信息
     * @return 评分
     */
    double getCourseRate(RateOrderVO rateOrderVO);

    /**
     * 获取属于教师的订单
     *
     * @param teacherId 教师ID
     * @return 课程-订单值对象
     */
    List<CourseOrderVO> getTeacherEarning(Integer teacherId);

    /**
     * 获取订单趋势
     *
     * @param courseId 课程ID
     * @return 订单趋势
     */
    List<OrderCumulativeVO> listCourseTrend(Integer courseId);

    /**
     * 管理员获取学生课程
     *
     * @param uid uid
     * @return 课程-订单值对象
     */
    List<StudentCourse> listStudentCourse(Integer uid);

    /**
     * 管理员获取教师课程
     *
     * @param uid uid
     * @return TeacherCourseVO
     */
    List<TeacherCourseVO> listTeacherCourse(Integer uid);

    /**
     * 获取系统订单Upper
     *
     * @return List<SysInnerOrder>
     */
    List<SysInnerOrder> listUpper();

    /**
     * 获取流通总学习点
     *
     * @return 总和
     */
    Double getAllDayEarning();

    /**
     * 获取课程好评率
     *
     * @param courseId 课程ID
     * @return 好评率
     */
    double getAcclaimCount(Integer courseId);

    /**
     * 获取课程用户评分
     *
     * @return List-UCC
     */
    List<UccVO> listUcc();
}
