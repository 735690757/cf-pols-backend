package com.karrycode.cfpolsbackend.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 提现申请表(Payout)实体类
 *
 * @author makejava
 * @since 2025-04-18 20:52:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("payout")
public class Payout {
    //唯一自增主键
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //教师外键
    private Integer userId;
    //教师昵称
    private String nickName;
    //减少的学习点数
    private String learnCount;
    //应当支付的钱数
    private String payout;
    //是否已经支付
    private Integer ispaid;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
}
