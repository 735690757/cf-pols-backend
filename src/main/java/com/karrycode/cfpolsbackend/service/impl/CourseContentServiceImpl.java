package com.karrycode.cfpolsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karrycode.cfpolsbackend.domain.dto.LearningProgressD;
import com.karrycode.cfpolsbackend.domain.po.CourseContent;
import com.karrycode.cfpolsbackend.mapper.CourseContentMapper;
import com.karrycode.cfpolsbackend.service.CourseContentService;
import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * 课程内容表(CourseContent)表服务实现类
 *
 * @author makejava
 * @since 2025-01-11 17:45:27
 */
@Service
public class CourseContentServiceImpl extends ServiceImpl<CourseContentMapper, CourseContent> implements CourseContentService {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 获取课程内容数量
     *
     * @param courseId 课程ID
     * @return 课程内容数量
     */
    @Override
    public int getContentCount(Integer courseId) {
        QueryWrapper<CourseContent> courseContentQueryWrapper = new QueryWrapper<>();
        courseContentQueryWrapper.eq("course_id", courseId);
        courseContentQueryWrapper.eq("is_delete", false);
        return (int) this.count(courseContentQueryWrapper);
    }

    /**
     * 更新用户的课程学习进度
     *
     * @param learningProgressD 进度对象
     * @return R
     */
    @Override
    public boolean updateCourseContentProgress(LearningProgressD learningProgressD) {
        Integer nowSection = learningProgressD.getNowSection();
        Integer allSection = learningProgressD.getAllSection();
        if (nowSection != null && allSection != null) {
            double percent = (double) nowSection / allSection;
            percent *= 100.0;
            // 保留小数点后两位
            percent = Math.round(percent * 100) / 100.0;
            learningProgressD.setPercent(percent);
        } else {
            learningProgressD.setPercent(0.0);
        }
        // 检查数据库中是否有（uid，cid）的记录
        // 创建 MongoDB 查询条件
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(learningProgressD.getUserId())
                .and("courseId").is(learningProgressD.getCourseId()));
        if (mongoTemplate.exists(query, LearningProgressD.class)) {
            // 如果有，则更新记录
            Update update = new Update();
            update.set("percent", learningProgressD.getPercent());
            update.set("nowSection", learningProgressD.getNowSection());
            update.set("allSection", learningProgressD.getAllSection());
            mongoTemplate.updateFirst(query, update, LearningProgressD.class);
        } else {
            // 如果没有，则插入新记录
            mongoTemplate.insert(learningProgressD);
        }
        return true;
    }

    /**
     * 获取用户的课程学习进度
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return LearningProgressD进度对象
     */
    @Override
    public LearningProgressD getCourseContentProgress(Integer userId, Integer courseId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)
                .and("courseId").is(courseId));
        if (mongoTemplate.exists(query, LearningProgressD.class)) {
            return mongoTemplate.findOne(query, LearningProgressD.class);
        } else {
            return new LearningProgressD(userId, courseId, -1, -1, 0.0);
        }
    }


}
