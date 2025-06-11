package com.karrycode.cfpolsbackend.controller;


import com.karrycode.cfpolsbackend.common.R;
import com.karrycode.cfpolsbackend.domain.po.Comment;
import com.karrycode.cfpolsbackend.domain.vo.UserCommentVO;
import com.karrycode.cfpolsbackend.service.CommentService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 评论区表(Comment)控制层
 *
 * @author makejava
 * @since 2025-01-11 17:44:54
 */
@RestController
@CrossOrigin
@RequestMapping("/comment")
public class CommentController {
    /**
     * 服务对象
     */
    @Autowired
    private CommentService commentService;

    /**
     * 发表评论
     *
     * @param comment 评论
     * @return R
     */

    @ApiOperation("发表评论")
    @PostMapping("/addComment")
    public R addComment(@RequestBody Comment comment) {
        return R.success(commentService.save(comment));
    }

    /**
     * 列出全部评论
     *
     * @return R
     */
    @ApiOperation("列出全部评论")
    @PostMapping("/listComment")
    public R listComment() {
        return R.success(commentService.list());
    }

    /**
     * 获取指定课程的评论区
     *
     * @param courseId 评论ID
     * @return R
     */
    @ApiOperation("获取指定课程的评论区")
    @GetMapping("/getComment/{courseId}")
    public R getComment(@PathVariable Integer courseId) {
        List<Comment> list = commentService.listCourseComment(courseId);
        return R.success(list);
    }

    /**
     * 删除评论
     *
     * @param id 评论ID
     * @return R
     */
    @ApiOperation("删除评论")
    @GetMapping("/deleteComment/{id}")
    public R deleteComment(@PathVariable Integer id) {
        Comment comment = commentService.getById(id);
        comment.setIsDelete(1);
        return R.success(commentService.updateById(comment));
    }

    @ApiOperation("恢复评论")
    @GetMapping("/recoverComment/{id}")
    public R recoverComment(@PathVariable Integer id) {
        Comment comment = commentService.getById(id);
        comment.setIsDelete(0);
        return R.success(commentService.updateById(comment));
    }

    /**
     * 点赞
     *
     * @param id 评论ID
     * @return R
     */
    @ApiOperation("点赞")
    @GetMapping("/likeComment/{id}")
    public R likeComment(@PathVariable Integer id) {
        Comment comment = commentService.getById(id);
        comment.setLike(comment.getLike() + 1);
        return R.success(commentService.updateById(comment));
    }

    /**
     * 获取教师课程的所有评论
     *
     * @param teacherId 教师ID
     * @return R
     */
    @ApiOperation("获取教师课程的所有评论")
    @GetMapping("/getTeacherComment")
    public R getTeacherComment(Integer teacherId) {
        List<Comment> list = commentService.listTeacherComment(teacherId);
        return R.success(list);
    }

    @ApiOperation("获取指定课程的所有评论（加花）")
    @GetMapping("/getCourseComment")
    public R getCourseComment(Integer courseId) {
        List<UserCommentVO> list = commentService.getUserComment(courseId);
        return R.success(list);
    }
}
