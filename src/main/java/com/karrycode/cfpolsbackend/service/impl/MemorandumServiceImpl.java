package com.karrycode.cfpolsbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karrycode.cfpolsbackend.mapper.MemorandumMapper;
import com.karrycode.cfpolsbackend.domain.po.Memorandum;
import com.karrycode.cfpolsbackend.service.MemorandumService;
import org.springframework.stereotype.Service;
 
/**
 * 备忘录表(Memorandum)表服务实现类
 *
 * @author makejava
 * @since 2025-01-11 17:46:02
 */
@Service
public class MemorandumServiceImpl extends ServiceImpl<MemorandumMapper, Memorandum> implements MemorandumService {

}
