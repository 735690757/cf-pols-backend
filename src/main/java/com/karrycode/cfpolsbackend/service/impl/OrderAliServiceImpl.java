package com.karrycode.cfpolsbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karrycode.cfpolsbackend.domain.po.OrderAli;
import com.karrycode.cfpolsbackend.domain.vo.ExtOrder;
import com.karrycode.cfpolsbackend.mapper.OrderAliMapper;
import com.karrycode.cfpolsbackend.service.OrderAliService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 支付记录(OrderAli)表服务实现类
 *
 * @author makejava
 * @since 2025-02-05 19:31:52
 */
@Service
public class OrderAliServiceImpl extends ServiceImpl<OrderAliMapper, OrderAli> implements OrderAliService {

    @Override
    public List<ExtOrder> listAll() {
        return baseMapper.listAll();
    }

    @Override
    public Integer getNowDayCount() {
        return baseMapper.getNowDayCount();
    }

    @Override
    public Double getSumNowDay() {
        return baseMapper.getSumNowDay();
    }
}
