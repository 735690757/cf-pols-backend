package com.karrycode.cfpolsbackend.mapper;

import com.karrycode.cfpolsbackend.CfPolsBackendApplication;
import com.karrycode.cfpolsbackend.CfPolsBackendApplicationTests;
import com.karrycode.cfpolsbackend.config.WebSocketConfig;
import com.karrycode.cfpolsbackend.domain.vo.UserCommentVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/4/20 14:21
 * @PackageName com.karrycode.cfpolsbackend.mapper
 * @ClassName CommentMapperTest
 * @Description
 * @Version 1.0
 */
class CommentMapperTest extends CfPolsBackendApplicationTests {
    @Autowired
    private CommentMapper commentMapper;

    @Test
    public void test() {
        List<UserCommentVO> userComment = commentMapper.getUserComment(27);
        System.out.println(userComment);
    }
}