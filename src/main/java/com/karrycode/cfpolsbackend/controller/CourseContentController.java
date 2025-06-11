package com.karrycode.cfpolsbackend.controller;


import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.karrycode.cfpolsbackend.common.R;
import com.karrycode.cfpolsbackend.config.MinioInfo;
import com.karrycode.cfpolsbackend.domain.dto.LearningProgressD;
import com.karrycode.cfpolsbackend.domain.po.CourseContent;
import com.karrycode.cfpolsbackend.service.CourseContentService;
import com.karrycode.cfpolsbackend.service.OrderService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * 课程内容表(CourseContent)控制层
 *
 * @author makejava
 * @since 2025-01-11 17:45:27
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/courseContent")
public class CourseContentController {
    /**
     * 服务对象
     */
    @Autowired
    private CourseContentService courseContentService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MinioInfo minioInfo;
    @Resource
    private MinioClient minioClient;

    /**
     * 根据课程ID获取课程小节
     *
     * @param courseID 课程ID
     * @return R
     */
    @ApiOperation("根据课程ID获取课程小节")
    @GetMapping("/getCourseSection")
    public R getCourseSection(Integer courseID) {
        QueryWrapper<CourseContent> courseContentQueryWrapper = new QueryWrapper<>();
        courseContentQueryWrapper.eq("course_id", courseID);
        courseContentQueryWrapper.eq("is_delete", false);
        return R.success(courseContentService.list(courseContentQueryWrapper));
    }

    /**
     * 添加课程小节
     *
     * @param courseContent 课程小节
     * @return R
     */
    @ApiOperation("添加课程小节")
    @PostMapping("/addCourseSection")
    public R addCourseSection(@RequestBody CourseContent courseContent) {
        boolean save = courseContentService.save(courseContent);
        if (!save) {
            return R.error("添加失败");
        }
        return R.success(courseContent.getId());
    }

    /**
     * 根据课程小节ID更新课程小节
     *
     * @param courseContent 课程小节
     * @return R
     */
    @ApiOperation("根据课程小节ID更新课程小节")
    @PostMapping("/updateCourseSection")
    public R updateCourseSection(@RequestBody CourseContent courseContent) {
        System.out.println(courseContent);
        return R.success(courseContentService.updateById(courseContent));
    }

    /**
     * 更新删除标志
     *
     * @param id           课程ID
     * @param deleteStatus 删除标志
     * @return R
     */
    @ApiOperation("更新删除标志")
    @GetMapping("/updateDeleteFlag")
    public R updateDeleteFlag(@RequestParam("id") Integer id,
                              @RequestParam("deleteStatus") boolean deleteStatus) {
        CourseContent deleteCC = courseContentService.getById(id);
        deleteCC.setDelete(deleteStatus);
        return R.success(courseContentService.updateById(deleteCC));
    }

    /**
     * 根据课程小节ID追加课程内容
     *
     * @param cid           课程ID
     * @param courseContent 课程小节
     * @return R
     * @throws Exception IOException
     */
    @ApiOperation("根据课程小节ID追加课程内容（MD5）")
    @PostMapping("/addCourseContent")
    public R addCourseContent(
            @RequestParam("cid") Integer cid,
            @RequestParam("courseContent") MultipartFile courseContent) throws Exception {
        CourseContent byId = courseContentService.getById(cid);
        String fileExt = Objects.requireNonNull(courseContent.getOriginalFilename()).
                substring(courseContent.getOriginalFilename().lastIndexOf("."));
        String fileMd5 = SecureUtil.md5(courseContent.getInputStream());
        String fileName = fileMd5 + fileExt;
        byId.setContent(fileName);
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(minioInfo.getBucketName())
                .stream(courseContent.getInputStream(), courseContent.getInputStream().available(), -1)
                .object(fileName)
                .build();
        minioClient.putObject(putObjectArgs);
        boolean update = courseContentService.updateById(byId);
        if (update) {
            return R.success(null, "上传成功", 200);
        }
        return R.error("上传失败");
    }

    /**
     * 指定课程id，获取课程内容数量，和好评率
     *
     * @param courseId 课程ID
     * @return R
     */
    @ApiOperation("指定课程id，获取课程内容数量，和好评率")
    @GetMapping("/getCourseContentCount")
    public R getCourseContentCount(Integer courseId) {
        double acclaimCount = orderService.getAcclaimCount(courseId);
        return R.success(courseContentService.getContentCount(courseId))
                .add("acclaimCount", acclaimCount);
    }

    /**
     * @param learningProgressD 记录对象
     * @return R
     */
    @ApiOperation("记录用户学习的课程进度")
    @PostMapping("/updateCourseContentProgress")
    public R updateCourseContentProgress(@RequestBody LearningProgressD learningProgressD) {
        return R.success(courseContentService.updateCourseContentProgress(learningProgressD));
    }

    /**
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return R
     */
    @ApiOperation("获取用户学习进度")
    @GetMapping("/getCourseContentProgress")
    public R getCourseContentProgress(@RequestParam Integer userId, @RequestParam Integer courseId) {
        LearningProgressD courseContentProgress = courseContentService.getCourseContentProgress(userId, courseId);
        if (courseContentProgress.getAllSection() == -1 && courseContentProgress.getNowSection() == -1) {
            return R.error("学习进度获取失败");
        } else {
            return R.success(courseContentProgress);
        }
    }

}
