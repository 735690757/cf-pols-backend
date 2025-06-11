package com.karrycode.cfpolsbackend.domain.po;

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
 * 订单表(Order)实体类
 *
 * @author makejava
 * @since 2025-01-11 17:46:24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("`order`")
public class Order {
    //唯一自增主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //用户外键
    private Integer userId;
    //课程外键
    private Integer courseId;
    //订单金额
    private String amount;
    //评分
    private String score;
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    @TableField(exist = false)
    private Date createTime;
}
