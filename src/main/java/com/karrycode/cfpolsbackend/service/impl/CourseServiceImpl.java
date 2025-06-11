package com.karrycode.cfpolsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karrycode.cfpolsbackend.domain.dto.AdminRecommendedD;
import com.karrycode.cfpolsbackend.domain.dto.LearningProgressD;
import com.karrycode.cfpolsbackend.domain.po.Course;
import com.karrycode.cfpolsbackend.domain.vo.PageObjectVO;
import com.karrycode.cfpolsbackend.domain.vo.PageVO;
import com.karrycode.cfpolsbackend.domain.vo.ScoreLevelVO;
import com.karrycode.cfpolsbackend.domain.vo.UserCourseVO;
import com.karrycode.cfpolsbackend.mapper.CourseMapper;
import com.karrycode.cfpolsbackend.service.CourseService;
import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 课程表(Course)表服务实现类
 *
 * @author makejava
 * @since 2025-01-11 17:45:11
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 浏览量加一
     *
     * @param courseId 课程ID
     * @return 操作是否成功
     */
    @Override
    public boolean addViewCount(Integer courseId) {
        Course course = this.baseMapper.selectById(courseId);
        if (course == null) {
            return false;
        }
        course.setViewCount(course.getViewCount() + 1);
        int update = this.baseMapper.updateById(course);
        return update > 0;
    }

    /**
     * 获取学习进度列表
     *
     * @param myCourseList 学过的课程
     * @param userId       用户ID
     * @return 进度对象列表
     */
    @Override
    public List<LearningProgressD> getLearningProgress(List<Course> myCourseList, Integer userId) {
        ArrayList<LearningProgressD> learningProgressDS = new ArrayList<>();
        // 从 myCourseList 中获取到每个课程的 id
        for (Course course : myCourseList) {
            // 获取到每个课程的 id
            Integer courseId = course.getId();
            // 从mongodb获取数据
            Query query = new Query();
            query.addCriteria(Criteria.where("courseId").is(courseId))
                    .addCriteria(Criteria.where("userId").is(userId));
            if (mongoTemplate.exists(query, LearningProgressD.class)) {
                LearningProgressD learningProgressD = mongoTemplate.findOne(query, LearningProgressD.class);
                learningProgressDS.add(learningProgressD);
            } else {
                learningProgressDS.add(new LearningProgressD(userId, courseId, -1, -1, 0.0));
            }
        }
        if (!learningProgressDS.isEmpty()) {
            return learningProgressDS;
        }
        return List.of();
    }

    /**
     * 获取分数扇形图
     *
     * @param courseId 课程ID
     * @return 分数扇形图
     */
    @Override
    public ScoreLevelVO getCourseScore(Integer courseId) {
        return baseMapper.getCourseScoreFan(courseId);
    }

    /**
     * 获取全部课程评分扇形图
     *
     * @return 分数扇形图
     */
    @Override
    public ScoreLevelVO getAllCourseScore() {
        return baseMapper.getAllCourseScoreFan();
    }

    @Override
    public List<Course> listFuzzy(String courseName) {
        return baseMapper.fuzzyList(courseName);
    }

    @Override
    public List<Course> listNotFree() {
        return baseMapper.listNotFree();
    }

    @Override
    public Integer getNowDayCount() {
        return baseMapper.getNowDayCount();
    }

    @Override
    public Course getTheMostViewCount() {
        return baseMapper.getMostPopularCourse();
    }

    @Override
    public boolean addRecommendCourse(Integer courseId) {
        Query query = new Query().addCriteria(Criteria.where("courseId").is(courseId));
        List<AdminRecommendedD> adminRecommendedDS = mongoTemplate.find(query, AdminRecommendedD.class);
        if (adminRecommendedDS.isEmpty()) {
            AdminRecommendedD adminRecommendedD = new AdminRecommendedD(courseId);
            mongoTemplate.insert(adminRecommendedD);
            return true;
        } else return adminRecommendedDS.size() == 1;
    }

    @Override
    public boolean removeRecommendCourse(Integer courseId) {
        Query query = new Query().addCriteria(Criteria.where("courseId").is(courseId));
        if (mongoTemplate.exists(query, AdminRecommendedD.class)) {
            mongoTemplate.remove(query, AdminRecommendedD.class);
            return true;
        }
        return false;
    }

    @Override
    public boolean isRecommendCourse(Integer courseId) {
        Query query = new Query().addCriteria(Criteria.where("courseId").is(courseId));
        return mongoTemplate.exists(query, AdminRecommendedD.class);
    }

    @Override
    public List<Course> getRecommendCourse() {
        List<AdminRecommendedD> recommendedDS = mongoTemplate.findAll(AdminRecommendedD.class);
        List<Integer> list = recommendedDS.stream().map(AdminRecommendedD::getCourseId).toList();
        return this.lambdaQuery().in(Course::getId, list).list();
    }

    @Override
    public PageObjectVO<Course> getCourseByPage(PageVO pageVO) {
        Long sizePage = pageVO.getSizePage();
        Long currentPage = pageVO.getCurrentPage();
        Page<Course> coursePage = new Page<>();
        coursePage.setCurrent(currentPage);
        coursePage.setSize(sizePage);
        Page<Course> paged = this.page(coursePage, new LambdaQueryWrapper<Course>()
                .eq(Course::getStatus, 1)
                .eq(Course::getIsDelete, 0)
                .orderByDesc(Course::getViewCount));
        if (paged != null) {
            List<Course> records = paged.getRecords();
            long total = paged.getTotal();
            return new PageObjectVO<>(total, records);
        }
        return null;
    }

    @Override
    public List<Course> sortByUserViewCount(List<Integer> courseIDList, Integer userId) {
        if (courseIDList.isEmpty()) {
            return List.of();
        }
        return baseMapper.sortByUserViewCount(courseIDList, userId);
    }

    @Override
    public List<UserCourseVO> listUserCourse(Integer userId1) {
        return baseMapper.listUserCourse(userId1);
    }
}
