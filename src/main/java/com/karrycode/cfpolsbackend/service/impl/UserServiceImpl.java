package com.karrycode.cfpolsbackend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karrycode.cfpolsbackend.domain.dto.UserLoginLogD;
import com.karrycode.cfpolsbackend.domain.po.OrderAli;
import com.karrycode.cfpolsbackend.domain.po.User;
import com.karrycode.cfpolsbackend.domain.vo.DailyLogTime;
import com.karrycode.cfpolsbackend.mapper.OrderAliMapper;
import com.karrycode.cfpolsbackend.mapper.UserMapper;
import com.karrycode.cfpolsbackend.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2025-01-11 17:47:40
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private OrderAliMapper orderAliMapper;

    /**
     * 获取登录日志
     *
     * @return 日志
     */
    @Override
    public ArrayList<DailyLogTime> getLogDateTimes() {
        String userId = StpUtil.getLoginId().toString();
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(Integer.parseInt(userId)));
        query.with(Sort.by(Sort.Direction.ASC, "loginTime"));
        List<UserLoginLogD> userLoginLogDList = mongoTemplate.find(query, UserLoginLogD.class);
        userLoginLogDList.forEach(userLoginLogD -> {
            String loginTime = userLoginLogD.getLoginTime();
            String[] split = loginTime.split(" ");
            userLoginLogD.setLoginTime(split[0]);
        });
        ArrayList<DailyLogTime> dailyLogTimes = new ArrayList<>();
        ArrayList<String> dailyLog = new ArrayList<>();
        for (UserLoginLogD logD : userLoginLogDList) {
            dailyLog.add(logD.getLoginTime());
        }
        String date = dailyLog.get(0);

        dailyLogTimes.add(new DailyLogTime(date, 0));
        for (String s : dailyLog) {
            if (s.equals(date)) {
                dailyLogTimes.get(dailyLogTimes.size() - 1).setTime(dailyLogTimes.get(dailyLogTimes.size() - 1).getTime() + 1);
            } else {
                date = s;
                dailyLogTimes.add(new DailyLogTime(date, 1));
            }
        }
        for (DailyLogTime dailyLogTime : dailyLogTimes) {
            dailyLogTime.setDate(dailyLogTime.getDate().substring(5));
        }
        return dailyLogTimes;
    }

    @Override
    public HashMap<String, Integer> getUserRatio() {
        HashMap<String, Integer> map = new HashMap<>();
        // 查询出STUDENT、TEACHER、ADMIN的数量
        this.getBaseMapper().selectList(null).forEach(user -> {
            String identity = user.getIdentity();
            if (map.containsKey(identity)) {
                map.put(identity, map.get(identity) + 1);
            } else {
                map.put(identity, 1);
            }
        });
        // 将大写的key转为小写
        map.forEach((k, v) -> {
            map.replace(k.toLowerCase(), v);
        });
        return map;
    }

    @Override
    public double getAccSum(String userId) {
        return orderAliMapper.getUserSum(userId);
    }
}
