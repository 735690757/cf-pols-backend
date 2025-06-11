package com.karrycode.cfpolsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.karrycode.cfpolsbackend.domain.dto.LearningProgressD;
import com.karrycode.cfpolsbackend.domain.po.CourseContent;

/**
 * 课程内容表(CourseContent)表服务接口
 *
 * @author makejava
 * @since 2025-01-11 17:45:27
 */
public interface CourseContentService extends IService<CourseContent> {
    /**
     * 获取课程内容数量
     *
     * @param courseId 课程ID
     * @return 课程内容数量
     */
    int getContentCount(Integer courseId);

    /**
     * 更新用户的课程学习进度
     *
     * @param learningProgressD 进度对象
     * @return R
     */
    boolean updateCourseContentProgress(LearningProgressD learningProgressD);

    /**
     * 获取用户的课程学习进度
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return LearningProgressD进度对象
     */
    LearningProgressD getCourseContentProgress(Integer userId, Integer courseId);


}
