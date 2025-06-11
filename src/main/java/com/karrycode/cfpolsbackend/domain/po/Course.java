package com.karrycode.cfpolsbackend.domain.po;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 课程表(Course)实体类
 *
 * @author makejava
 * @since 2025-01-11 17:45:11
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("course")
public class Course {
    //唯一自增主键
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelProperty("课程ID")
    private Integer id;
    //教师id外键
    @ExcelProperty("教师ID")
    private Integer teacherId;
    //课程封面
    @ExcelIgnore
    private String cover;
    //课程名
    @ExcelProperty("课程名")
    private String title;
    //课程描述
    @ExcelProperty("课程描述")
    private String courseDescribe;
    //课程发布状态
    @ExcelProperty("课程发布状态")
    private Integer status;
    //浏览数量
    @ExcelProperty("浏览数量")
    private Integer viewCount;
    //购买数量
    @ExcelProperty("购买数量")
    private Integer buyCount;
    //价格
    @ExcelProperty("价格")
    private String price;
    //逻辑删除
    @ExcelIgnore
    private Integer isDelete;
    //创建时间
    @ExcelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
}
