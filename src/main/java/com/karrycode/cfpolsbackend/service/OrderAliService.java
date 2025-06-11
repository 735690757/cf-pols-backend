package com.karrycode.cfpolsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.karrycode.cfpolsbackend.domain.po.OrderAli;
import com.karrycode.cfpolsbackend.domain.vo.ExtOrder;

import java.util.List;

/**
 * 支付记录(OrderAli)表服务接口
 *
 * @author makejava
 * @since 2025-02-05 19:31:52
 */
public interface OrderAliService extends IService<OrderAli> {

    List<ExtOrder> listAll();

    Integer getNowDayCount();

    Double getSumNowDay();
}
