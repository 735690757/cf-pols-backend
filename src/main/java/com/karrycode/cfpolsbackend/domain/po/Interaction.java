package com.karrycode.cfpolsbackend.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 交互表(Interaction)实体类
 *
 * @author makejava
 * @since 2025-01-11 17:45:43
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("interaction")
public class Interaction {
    //唯一自增主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //用户外键
    private Integer userId;
    //课程外键
    private Integer courseId;
    //浏览次数
    private Integer viewCount;
}
