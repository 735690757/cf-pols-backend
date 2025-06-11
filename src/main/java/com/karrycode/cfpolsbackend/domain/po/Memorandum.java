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
 * 备忘录表(Memorandum)实体类
 *
 * @author makejava
 * @since 2025-01-11 17:46:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("memorandum")
public class Memorandum {
    //唯一自增主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //用户外键
    private Integer userId;
    //备忘录内容
    private String content;
    //状态
    private Integer status;
    //逻辑删除
    private Integer isDelete;
    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
}
