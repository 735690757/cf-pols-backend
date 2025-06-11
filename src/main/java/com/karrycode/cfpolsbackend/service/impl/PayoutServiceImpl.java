package com.karrycode.cfpolsbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karrycode.cfpolsbackend.mapper.PayoutMapper;
import com.karrycode.cfpolsbackend.domain.po.Payout;
import com.karrycode.cfpolsbackend.service.PayoutService;
import org.springframework.stereotype.Service;
 
/**
 * 提现申请表(Payout)表服务实现类
 *
 * @author makejava
 * @since 2025-04-18 20:52:24
 */
@Service
public class PayoutServiceImpl extends ServiceImpl<PayoutMapper, Payout> implements PayoutService {

}
