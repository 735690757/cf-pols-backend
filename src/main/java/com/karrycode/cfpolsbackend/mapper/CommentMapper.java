package com.karrycode.cfpolsbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.karrycode.cfpolsbackend.domain.po.Comment;
import com.karrycode.cfpolsbackend.domain.vo.UserCommentVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * 评论区表(Comment)表数据库访问层
 *
 * @author makejava
 * @since 2025-01-11 17:44:54
 */
public interface CommentMapper extends BaseMapper<Comment> {
    @Select("select u.id uid, nick_name, avatar, content, score, `like`,is_delete, co.create_time\n" +
            "from comment co,\n" +
            "     user u,\n" +
            "     `order` o\n" +
            "where co.course_id = #{courseId}\n" +
            "  and co.user_id = u.id\n" +
            "  and o.course_id = #{courseId}\n" +
            "  and o.user_id = u.id")
    List<UserCommentVO> getUserComment(Integer courseId);
}
