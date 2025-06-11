package com.karrycode.cfpolsbackend.controller;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.karrycode.cfpolsbackend.common.R;
import com.karrycode.cfpolsbackend.config.MinioInfo;
import com.karrycode.cfpolsbackend.domain.po.Course;
import com.karrycode.cfpolsbackend.domain.po.User;
import com.karrycode.cfpolsbackend.domain.vo.PageObjectVO;
import com.karrycode.cfpolsbackend.domain.vo.PageVO;
import com.karrycode.cfpolsbackend.domain.vo.UserCourseVO;
import com.karrycode.cfpolsbackend.service.CourseService;
import com.karrycode.cfpolsbackend.service.UserService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 课程表(Course)控制层
 *
 * @author makejava
 * @since 2025-01-11 17:45:11
 */
@CrossOrigin
@RestController
@RequestMapping("/course")
public class CourseController {
    /**
     * 服务对象
     */
    @Autowired
    private CourseService courseService;
    @Autowired
    private MinioInfo minioInfo;
    @Resource
    private MinioClient minioClient;
    @Autowired
    private UserService userService;
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 添加课程
     *
     * @param course 课程
     * @return R
     */
    @ApiOperation("添加课程")
    @PostMapping("/addCourse")
    public R addCourse(@RequestBody Course course) {
        int loginIdAsInt = StpUtil.getLoginIdAsInt();
        course.setTeacherId(loginIdAsInt);
        boolean save = courseService.save(course);
        if (!save) {
            return R.error("添加失败");
        }
        return R.success(course.getId());
    }

    /**
     * 追加课程封面
     *
     * @param courseCover 封面
     * @param courseId    课程ID
     * @return R
     * @throws Exception IO异常
     */
    @ApiOperation("追加课程封面")
    @PostMapping("/addCourseCover")
    public R addCourseCover(@RequestParam("courseCover") MultipartFile courseCover,
                            @RequestParam("courseId") Integer courseId) throws Exception {
        String fileExt = Objects.requireNonNull(courseCover.getOriginalFilename()).
                substring(courseCover.getOriginalFilename().lastIndexOf("."));
        String fileMd5 = SecureUtil.md5(courseCover.getInputStream());
        String fileName = fileMd5 + fileExt;
        System.out.println(fileName);
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(minioInfo.getBucketName())
                .stream(courseCover.getInputStream(), courseCover.getInputStream().available(), -1)
                .object(fileName)
                .build();
        minioClient.putObject(putObjectArgs);
        LambdaQueryWrapper<Course> lambdaQueryWrapper = new LambdaQueryWrapper<Course>()
                .eq(Course::getId, courseId);

        courseService.updateById(
                Course.builder()
                        .id(courseId)
                        .cover(fileName).build()
        );
        return R.success(null, "上传成功", 200);
    }

    /**
     * 获取当前老师的所有课程
     *
     * @return R
     */
    @ApiOperation("获取当前老师的所有课程")
    @GetMapping("/getCourseList")
    public R getCourseList() {
        int loginIdAsInt = StpUtil.getLoginIdAsInt();
        LambdaQueryWrapper<Course> lambdaQueryWrapper = new LambdaQueryWrapper<Course>()
                .eq(Course::getTeacherId, loginIdAsInt)
                .eq(Course::getIsDelete, 0);
        return R.success(courseService.list(lambdaQueryWrapper));
    }

    /**
     * 根据课程ID更新课程
     *
     * @param course 课程
     * @return R
     */
    @ApiOperation("根据课程ID更新课程")
    @PostMapping("/updateCourse")
    public R updateCourse(@RequestBody Course course) {
        boolean update = courseService.updateById(course);
        if (!update) {
            return R.error("更新失败");
        }
        return R.success("更新成功");
    }

    /**
     * 根据课程ID更新课程状态
     *
     * @param course 课程
     * @return R
     */
    @ApiOperation("根据课程ID更新课程状态")
    @PostMapping("/updateCourseStatus")
    public R updateCourseStatus(@RequestBody Course course) {
        LambdaUpdateWrapper<Course> courseLambdaUpdateWrapper = new LambdaUpdateWrapper<>();

        boolean update = courseService.update(
                courseLambdaUpdateWrapper
                        .eq(Course::getId, course.getId())
                        .set(Course::getStatus, course.getStatus())
        );
        if (!update) {
            return R.error("更新失败");
        }
        return R.success("更新成功");
    }

    /**
     * 根据课程ID删除课程(逻辑删除)
     *
     * @param courseID 课程ID
     * @return R
     */
    @ApiOperation("根据课程ID删除课程(逻辑删除)")
    @GetMapping("/deleteCourse")
    public R deleteCourse(Integer courseID) {
        LambdaUpdateWrapper<Course> courseLambdaUpdateWrapper = new LambdaUpdateWrapper<>();

        boolean update = courseService.update(
                courseLambdaUpdateWrapper
                        .eq(Course::getId, courseID)
                        .set(Course::getIsDelete, 1)
        );
        if (!update) {
            return R.error("删除失败");
        }
        return R.success("删除成功");
    }

    /**
     * 获得该教师的课程名及浏览量等数据（ECharts）
     *
     * @return R
     */
    @ApiOperation("获得该教师的课程名及浏览量等数据（ECharts）")
    @GetMapping("/getCourseDataECharts")
    public R getCourseData() {
        int loginIdAsInt = StpUtil.getLoginIdAsInt();
        LambdaQueryWrapper<Course> courseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        courseLambdaQueryWrapper.eq(Course::getTeacherId, loginIdAsInt)
                .orderByDesc(Course::getViewCount);
        List<Course> list = courseService.list(courseLambdaQueryWrapper);
        List<String> courseTitleList = list.stream().map(Course::getTitle).toList();
        List<Integer> viewCountList = list.stream().map(Course::getViewCount).toList();
        List<Integer> buyCountList = list.stream().map(Course::getBuyCount).toList();
        List<String> priceList = list.stream().map(Course::getPrice).toList();

        return R.success("success")
                .add("courseTitleList", courseTitleList)
                .add("viewCountList", viewCountList)
                .add("buyCountList", buyCountList)
                .add("priceList", priceList);
    }

    /**
     * 获取所有已发布的课程
     *
     * @return R
     */
    @ApiOperation("获取所有已发布的课程")
    @GetMapping("/getAllPublishCourse")
    public R getAllPublishCourse() {
        LambdaQueryWrapper<Course> courseLambdaQueryWrapper = new LambdaQueryWrapper<>();
        courseLambdaQueryWrapper.eq(Course::getStatus, 1)
                .eq(Course::getIsDelete, 0);
        List<Course> list = courseService.list(courseLambdaQueryWrapper);
        List<String> teacherAvatar = new ArrayList<>();
        list.forEach(course -> {
            User user = userService.getById(course.getTeacherId());
            teacherAvatar.add(user.getAvatar());
        });
        return R.success(list).add("teacherAvatar", teacherAvatar);
    }

    /**
     * 根据课程ID获取课程信息
     *
     * @param courseId 课程ID
     * @return R
     */
    @ApiOperation("根据课程ID获取课程信息")
    @GetMapping("/getCourseById")
    public R getCourseById(Integer courseId) {
        Course course = courseService.getById(courseId);
        return R.success(course);
    }

    /**
     * 根据课程ID增加浏览量
     *
     * @param courseId 课程ID
     * @return R
     */
    @ApiOperation("根据课程ID增加浏览量")
    @GetMapping("/addViewCount")
    public R addViewCount(Integer courseId) {
        return R.success(courseService.addViewCount(courseId));
    }

    /**
     * 根据课程id获取课程费用
     *
     * @param courseId 课程ID
     * @return R
     */
    @ApiOperation("根据课程id获取课程费用")
    @GetMapping("/getCoursePrice/{courseId}")
    public R getCoursePrice(@PathVariable Integer courseId) {
        Course course = courseService.getById(courseId);
        return R.success(course.getPrice());
    }

    /**
     * 根据课程id获取课程信息
     *
     * @param courseId 课程ID
     * @return R
     */
    @ApiOperation("根据课程id获取课程信息")
    @GetMapping("/getCourseInfo/{courseId}")
    public R getCourseInfo(@PathVariable Integer courseId) {
        Course course = courseService.getById(courseId);
        return R.success(course);
    }

    /**
     * 获得所有的课程名及浏览量等数据
     *
     * @return R
     */
    @ApiOperation("获得所有的课程名及浏览量等数据（ECharts）")
    @GetMapping("/getAllCourseDataECharts")
    public R getCourseAllData() {
        List<Course> list = courseService.list();
        List<String> courseTitleList = list.stream().map(Course::getTitle).toList();
        List<Integer> viewCountList = list.stream().map(Course::getViewCount).toList();
        List<Integer> buyCountList = list.stream().map(Course::getBuyCount).toList();
        List<String> priceList = list.stream().map(Course::getPrice).toList();
        return R.success("success")
                .add("courseTitleList", courseTitleList)
                .add("viewCountList", viewCountList)
                .add("buyCountList", buyCountList)
                .add("priceList", priceList);
    }

    /**
     * 管理员获取全部课程信息
     *
     * @return R
     */
    @ApiOperation("获取全部课程信息")
    @GetMapping("/getAllCourse")
    public R getAllCourse() {
        List<Course> list = courseService.lambdaQuery().eq(Course::getIsDelete, 0).eq(Course::getStatus, 1).list();
        return R.success(list);
    }

    /**
     * 模糊查询
     *
     * @param courseName 课程名
     * @return R
     */
    @ApiOperation("课程模糊查询")
    @GetMapping("/getCourseByFuzzy")
    public R getCourseByFuzzy(String courseName) {
        List<Course> list = courseService.listFuzzy(courseName);
        return R.success(list);
    }

    /**
     * 只看付费课程
     *
     * @return R
     */
    @ApiOperation("只看付费课程")
    @GetMapping("/getPayCourse")
    public R getPayCourse() {
        List<Course> list = courseService.listNotFree();
        return R.success(list);
    }

    /**
     * 将指定课程加入推荐中
     *
     * @param courseId 课程ID
     * @return R
     */
    @ApiOperation("将指定课程加入推荐中")
    @GetMapping("/addRecommendCourse")
    public R addRecommendCourse(Integer courseId) {
        boolean update = courseService.addRecommendCourse(courseId);
        if (!update) {
            return R.error("添加失败");
        }
        return R.success("添加成功");
    }

    /**
     * 将指定课程从推荐列表中移除
     *
     * @param courseId 课程ID
     * @return R
     */
    @ApiOperation("将指定课程从推荐列表中移除")
    @GetMapping("/removeRecommendCourse")
    public R removeRecommendCourse(Integer courseId) {
        boolean update = courseService.removeRecommendCourse(courseId);
        if (!update) {
            return R.error("移除失败");
        }
        return R.success("移除成功");
    }

    /**
     * 检查指定课程是否为推荐课程
     *
     * @param courseId 课程ID
     * @return R
     */
    @ApiOperation("检查指定课程是否为推荐课程")
    @GetMapping("/checkRecommendCourse")
    public R checkRecommendCourse(Integer courseId) {
        boolean isRecommend = courseService.isRecommendCourse(courseId);
        return R.success(isRecommend);
    }

    /**
     * 获取所有推荐课程
     *
     * @return R
     */
    @ApiOperation("获取所有推荐课程")
    @GetMapping("/getRecommendCourse")
    public R getRecommendCourse() {
        List<Course> list = courseService.getRecommendCourse();
        return R.success(list);
    }

    /**
     * 分页获取课程
     *
     * @param pageVO 分页信息
     * @return R
     */
    @ApiOperation("分页获取课程")
    @PostMapping("/getCourseByPage")
    public R getCourseByPage(@RequestBody PageVO pageVO) {
        PageObjectVO<Course> pageObjectVO = courseService.getCourseByPage(pageVO);
        Integer allPage = Math.toIntExact(pageObjectVO.getTotalCount() / pageVO.getSizePage() + 1);
        List<String> teacherAvatar = new ArrayList<>();
        pageObjectVO.getObjectList().forEach(course -> {
            User user = userService.getById(course.getTeacherId());
            teacherAvatar.add(user.getAvatar());
        });
        return R.success(pageObjectVO)
                .add("allPage", allPage)
                .add("teacherAvatar", teacherAvatar);
    }

    @ApiOperation("传入两个用户id，分别返回他们所学过的课程")
    @GetMapping("/getUserCourseDoubleID")
    public R getUserCourseDoubleID(Integer userId1, Integer userId2) {
        List<UserCourseVO> userCourseVOS1 = courseService.listUserCourse(userId1);
        List<UserCourseVO> userCourseVOS2 = courseService.listUserCourse(userId2);
        return R.success("success")
                .add("userCourseVOS1", userCourseVOS1)
                .add("userCourseVOS2", userCourseVOS2);
    }
}
