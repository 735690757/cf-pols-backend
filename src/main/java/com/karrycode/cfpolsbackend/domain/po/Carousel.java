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

/**
 * 轮播图表(Carousel)实体类
 *
 * @author makejava
 * @since 2025-01-11 17:43:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("carousel")
public class Carousel {
    //唯一自增主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //图片路径
    private String photo;
    //排序字
    private Integer sort;
    //轮播图发布状态
    private Integer status;
    //逻辑删除
    private Integer isDelete;
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date createTime;
    //修改时间
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date modifyTime;
}
