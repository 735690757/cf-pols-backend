package com.karrycode.cfpolsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.karrycode.cfpolsbackend.domain.po.User;
import com.karrycode.cfpolsbackend.domain.vo.DailyLogTime;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 用户表(User)表服务接口
 *
 * @author makejava
 * @since 2025-01-11 17:47:40
 */
public interface UserService extends IService<User> {
    /**
     * 获取登录日志
     *
     * @return 日志
     */
    ArrayList<DailyLogTime> getLogDateTimes();

    HashMap<String, Integer> getUserRatio();

    /**
     * 获取累计充值金额
     *
     * @param userId 用户id
     * @return 累计充值金额
     */
    double getAccSum(String userId);
}
