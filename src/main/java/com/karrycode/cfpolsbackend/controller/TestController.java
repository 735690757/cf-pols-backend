package com.karrycode.cfpolsbackend.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.karrycode.cfpolsbackend.domain.eo.IdentityE;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/1/11 21:15
 * @PackageName com.karrycode.cfpolsbackend.controller
 * @ClassName TestController
 * @Description 测试用
 * @Version 1.0
 */
@CrossOrigin
@RestController
@RequestMapping("/test")
public class TestController {
    /**
     *
     * @return 测试结果
     */
    @SaCheckRole("ADMIN")
    @ApiOperation("你好")
    @RequestMapping("/hello")
    public String hello(){
        return "he啊多发点发达地方llo!!";
    }
    /**
     *
     * @return 测试结果
     */
    @ApiOperation("鉴权测试")
    @GetMapping("/auth")
    public String auth(){
        boolean adminAU = StpUtil.hasRole(String.valueOf(IdentityE.STUDENT));
        return "鉴权测试::"+adminAU;
    }
    /**
     *
     * @return 测试结果
     */
    @ApiOperation("获取当前用户测试")
    @GetMapping("/getUser")
    public String getUser(){
        return "当前用户::"+StpUtil.getLoginId();
    }
}
