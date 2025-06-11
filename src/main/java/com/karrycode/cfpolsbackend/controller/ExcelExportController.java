package com.karrycode.cfpolsbackend.controller;

import com.alibaba.excel.EasyExcel;
import com.karrycode.cfpolsbackend.domain.po.User;
import com.karrycode.cfpolsbackend.service.CourseService;
import com.karrycode.cfpolsbackend.service.UserService;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/5/16 17:13
 * @PackageName com.karrycode.cfpolsbackend.controller
 * @ClassName ExcelExportController
 * @Description
 * @Version 1.0
 */

@CrossOrigin
@RestController
@RequestMapping("/excel")
public class ExcelExportController {
    @Resource
    private UserService userService;
    @Resource
    private CourseService courseService;

    @ApiOperation("生成管理员用户Excel")
    @GetMapping("/admin")
    public void adminUserExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=adminUserExport.xlsx");

        EasyExcel.write(response.getOutputStream(), User.class)
                .sheet("管理员用户")
                .doWrite(() -> userService.lambdaQuery().eq(User::getIdentity, "ADMIN").list());
    }

    @ApiOperation("生成教师用户Excel")
    @GetMapping("/teacher")
    public void teacherUserExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=teacherUserExport.xlsx");

        EasyExcel.write(response.getOutputStream(), User.class)
                .sheet("教师用户")
                .doWrite(() -> userService.lambdaQuery().eq(User::getIdentity, "TEACHER").list()
                        .stream()
                        .peek(user -> user.setPassword("******"))
                        .toList());
    }

    @ApiOperation("生成学生用户Excel")
    @GetMapping("/student")
    public void studentUserExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=studentUserExport.xlsx");

        EasyExcel.write(response.getOutputStream(), User.class)
                .sheet("学生用户")
                .doWrite(() -> userService.lambdaQuery().eq(User::getIdentity, "STUDENT").list()
                        .stream()
                        .peek(user -> user.setPassword("******"))
                        .toList());
    }

    @ApiOperation("导出全部课程到Excel")
    @GetMapping("/course")
    public void courseExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=courseExport.xlsx");

        EasyExcel.write(response.getOutputStream(), com.karrycode.cfpolsbackend.domain.po.Course.class)
                .sheet("课程信息")
                .doWrite(() -> courseService.list());
    }
}
