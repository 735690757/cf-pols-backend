package com.karrycode.cfpolsbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.karrycode.cfpolsbackend.domain.po.Interaction;

/**
 * 交互表(Interaction)表服务接口
 *
 * @author makejava
 * @since 2025-01-11 17:45:43
 */
public interface InteractionService extends IService<Interaction> {
    /**
     * 交互方法
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     */
    void interaction(String userId, String courseId);
}
