package com.karrycode.cfpolsbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.karrycode.cfpolsbackend.domain.po.User;
import org.apache.ibatis.annotations.Select;


/**
 * 用户表(User)表数据库访问层
 *
 * @author makejava
 * @since 2025-01-11 17:47:40
 */
public interface UserMapper extends BaseMapper<User> {

}
