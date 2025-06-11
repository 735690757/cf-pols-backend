package com.karrycode.cfpolsbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karrycode.cfpolsbackend.domain.po.Interaction;
import com.karrycode.cfpolsbackend.mapper.InteractionMapper;
import com.karrycode.cfpolsbackend.service.InteractionService;
import org.springframework.stereotype.Service;

/**
 * 交互表(Interaction)表服务实现类
 *
 * @author makejava
 * @since 2025-01-11 17:45:43
 */
@Service
public class InteractionServiceImpl extends ServiceImpl<InteractionMapper, Interaction> implements InteractionService {
    /**
     * 交互方法
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     */
    @Override
    public void interaction(String userId, String courseId) {
        // 先从数据库查询是否有userid -> courseid的记录，如果有则更新，如果没有则插入
        Interaction interaction = this.getOne(new LambdaQueryWrapper<Interaction>()
                .eq(Interaction::getUserId, userId)
                .eq(Interaction::getCourseId, courseId));
        if (interaction == null) {
            interaction = Interaction.builder()
                    .userId(Integer.parseInt(userId))
                    .courseId(Integer.parseInt(courseId))
                    .viewCount(1)
                    .build();
            this.save(interaction);
        } else {
            interaction.setViewCount(interaction.getViewCount() + 1);
            this.updateById(interaction);
        }
    }
}
