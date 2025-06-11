package com.karrycode.cfpolsbackend.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 课程内容表(CourseContent)实体类
 *
 * @author makejava
 * @since 2025-01-11 17:45:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@TableName("course_content")
public class CourseContent {
    //唯一自增主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //所属课程id
    private Integer courseId;
    //文件标题
    private String title;
    //排序字段
    private Integer sort;
    //是否是视频
    @JsonProperty("isVideo")
    private Integer isVideo;
    //文件内容
    private String content;
    //逻辑删除
    private boolean isDelete;
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date createTime;
}
