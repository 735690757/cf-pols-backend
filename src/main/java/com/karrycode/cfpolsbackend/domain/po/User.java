package com.karrycode.cfpolsbackend.domain.po;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 用户表(User)实体类
 *
 * @author makejava
 * @since 2025-01-11 17:47:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@TableName("user")
public class User {
    //唯一自增主键
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelProperty("用户ID")
    private Integer id;
    //用户名
    @ExcelProperty("用户名")
    private String userName;
    //用户昵称
    @ExcelProperty("用户昵称")
    private String nickName;
    //用户密码
    @ExcelProperty("用户密码")
    private String password;
    //用户头像
    @ExcelIgnore
    private String avatar;
    //用户身份
    @ExcelProperty("用户身份")
    private String identity;
    //用户余额
    @ExcelProperty("用户余额")
    private String fund;
    //封号标志
    @ExcelProperty("封号标志")
    private Boolean isDisabled;
    //创建日期
    @ExcelProperty("创建日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    //修改时间
    @ExcelProperty("修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date modifyTime;
}
