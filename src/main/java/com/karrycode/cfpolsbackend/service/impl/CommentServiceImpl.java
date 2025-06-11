package com.karrycode.cfpolsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karrycode.cfpolsbackend.domain.po.Comment;
import com.karrycode.cfpolsbackend.domain.po.Course;
import com.karrycode.cfpolsbackend.domain.vo.UserCommentVO;
import com.karrycode.cfpolsbackend.mapper.CommentMapper;
import com.karrycode.cfpolsbackend.service.CommentService;
import com.karrycode.cfpolsbackend.service.CourseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论区表(Comment)表服务实现类
 *
 * @author makejava
 * @since 2025-01-11 17:44:54
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private CourseService courseService;

    /**
     * 获取课程评论
     *
     * @param courseId 课程ID
     * @return 课程评论列表
     */
    @Override
    public List<Comment> listCourseComment(Integer courseId) {
        // 获取顶级评论（reid为空的）
        LambdaQueryWrapper<Comment> topCommentsQuery = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getCourseId, courseId)
                .eq(Comment::getIsDelete, 0)
                .isNull(Comment::getReId)
                .orderByDesc(Comment::getLike);
        List<Comment> topCommentList = this.list(topCommentsQuery);
        // 遍历顶级评论，获取子评论
        for (Comment topComment : topCommentList) {
            LambdaQueryWrapper<Comment> childQueryWrapper = new LambdaQueryWrapper<Comment>()
                    .eq(Comment::getIsDelete, 0)
                    .eq(Comment::getReId, topComment.getId());
            List<Comment> childCommentList = this.list(childQueryWrapper);
            topComment.setChildren(childCommentList);
        }
        return topCommentList;
    }

    @Override
    public List<Comment> listTeacherComment(Integer teacherId) {
        // 查询教师所有课程
        List<Integer> courseIDList = courseService.lambdaQuery()
                .eq(Course::getTeacherId, teacherId)
                .list().stream().map(Course::getId).toList();
        if (courseIDList.isEmpty()) {
            return List.of();
        }
        return this.lambdaQuery().orderByDesc(Comment::getCreateTime).in(Comment::getCourseId, courseIDList)
                .list();
    }

    @Override
    public List<UserCommentVO> getUserComment(Integer courseId) {
        return baseMapper.getUserComment(courseId);
    }
}
