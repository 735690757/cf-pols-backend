package com.karrycode.cfpolsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.karrycode.cfpolsbackend.domain.po.Comment;
import com.karrycode.cfpolsbackend.domain.vo.UserCommentVO;

import java.util.List;

/**
 * 评论区表(Comment)表服务接口
 *
 * @author makejava
 * @since 2025-01-11 17:44:54
 */
public interface CommentService extends IService<Comment> {
    /**
     * 获取课程评论
     * @param courseId 课程ID
     * @return 课程评论列表
     */
    List<Comment> listCourseComment(Integer courseId);

    List<Comment> listTeacherComment(Integer teacherId);

    List<UserCommentVO> getUserComment(Integer courseId);
}
