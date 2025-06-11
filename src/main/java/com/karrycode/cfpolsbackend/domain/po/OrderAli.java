package com.karrycode.cfpolsbackend.domain.po;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/2/5 18:03
 * @PackageName com.karrycode.cfpolsbackend.domain.po
 * @ClassName OrderAli
 * @Description
 * @Version 1.0
 */
@Data
@Builder
@TableName("order_ali")
public class OrderAli implements Serializable {
    /**
     * 订单Id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 用户Id
     */
    private Long userId;
    /**
     * 接口Id
     */
    private Long interfaceInfoId;
    /**
     * 支付金额
     */
    private Double money;
    /**
     * 支付方式
     */
    private String paymentMethod;
    /**
     * 0 - 未支付 1 - 已支付
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除
     */
    private Integer isDelete;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
