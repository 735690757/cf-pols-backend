package com.karrycode.cfpolsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.karrycode.cfpolsbackend.domain.dto.LearningProgressD;
import com.karrycode.cfpolsbackend.domain.po.Course;
import com.karrycode.cfpolsbackend.domain.vo.PageObjectVO;
import com.karrycode.cfpolsbackend.domain.vo.PageVO;
import com.karrycode.cfpolsbackend.domain.vo.ScoreLevelVO;
import com.karrycode.cfpolsbackend.domain.vo.UserCourseVO;

import java.util.List;

/**
 * 课程表(Course)表服务接口
 *
 * @author makejava
 * @since 2025-01-11 17:45:11
 */
public interface CourseService extends IService<Course> {
    /**
     * 浏览量加一
     *
     * @param courseId 课程ID
     * @return 操作是否成功
     */
    boolean addViewCount(Integer courseId);

    /**
     * 获取学习进度列表
     *
     * @param myCourseList 学过的课程
     * @param uid          用户ID
     * @return 进度对象列表
     */
    List<LearningProgressD> getLearningProgress(List<Course> myCourseList, Integer uid);

    /**
     * 获取指定课程分数扇形图
     *
     * @param courseId 课程ID
     * @return 扇形图数据
     */
    ScoreLevelVO getCourseScore(Integer courseId);

    /**
     * 获取全部课程分数扇形图
     *
     * @return 扇形图数据
     */
    ScoreLevelVO getAllCourseScore();

    /**
     * 课程模糊查询
     *
     * @param courseName 课程名
     * @return 模糊查询结果
     */
    List<Course> listFuzzy(String courseName);

    /**
     * 获取全部课程，但排除免费课程
     *
     * @return 全部课程
     */
    List<Course> listNotFree();

    /**
     * 今天课程购买量
     *
     * @return 购买量
     */
    Integer getNowDayCount();

    /**
     * 最受欢迎的课程
     *
     * @return 课程
     */
    Course getTheMostViewCount();

    /**
     * 添加课程到推荐列表
     *
     * @param courseId 课程ID
     * @return 结果
     */
    boolean addRecommendCourse(Integer courseId);

    /**
     * 删除课程从推荐列表
     *
     * @param courseId 课程ID
     * @return 结果
     */
    boolean removeRecommendCourse(Integer courseId);

    /**
     * 检查课程是否在推荐列表中
     *
     * @param courseId 课程ID
     * @return 结果
     */
    boolean isRecommendCourse(Integer courseId);

    /**
     * 获取推荐课程
     *
     * @return 课程列表
     */
    List<Course> getRecommendCourse();

    /**
     * 分页获取推荐课程
     *
     * @param pageVO 分页信息
     * @return 分页对象
     */
    PageObjectVO<Course> getCourseByPage(PageVO pageVO);

    /**
     * 根据用户ID和课程ID列表对课程列表进行排序
     *
     * @param courseIDList 课程ID列表
     * @param userId 用户ID
     * @return 排序后的课程列表
     */
    List<Course> sortByUserViewCount(List<Integer> courseIDList, Integer userId);

    /**
     * 获取用户和课程的关联信息
     *
     * @param userId1 用户ID1
     * @return 信息
     */
    List<UserCourseVO> listUserCourse(Integer userId1);
}
