package com.karrycode.cfpolsbackend.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 评论区表(Comment)实体类
 *
 * @author makejava
 * @since 2025-01-11 17:44:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("comment")
public class Comment {
    //唯一自增主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //用户外键
    private Integer userId;
    //用户名
    private String userName;
    //自关联回复索引
    private Integer reId;
    //回复目标名
    private String target;
    //所属课程外键
    private Integer courseId;
    //回复内容
    private String content;
    // 点赞数
    @TableField("`like`")
    private Integer like;
    //逻辑删除
    private Integer isDelete;
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    @TableField(exist = false)
    private List<Comment> children;
}
