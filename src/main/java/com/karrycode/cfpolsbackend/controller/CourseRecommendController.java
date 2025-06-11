package com.karrycode.cfpolsbackend.controller;

import com.karrycode.cfpolsbackend.common.R;
import com.karrycode.cfpolsbackend.domain.po.Course;
import com.karrycode.cfpolsbackend.service.CourseRecommendService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/4/9 09:28
 * @PackageName com.karrycode.cfpolsbackend.controller
 * @ClassName CourseRecommendController
 * @Description
 * @Version 1.0
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/courseRecommendCont")
public class CourseRecommendController {
    @Autowired
    private CourseRecommendService courseRecommendService;

    /**
     * @param userId 用户ID
     * @return R
     */
    @ApiOperation("推荐课程")
    @GetMapping("/getRecommendCourse")
    public R getRecommendCourse(@RequestParam Integer userId) {
        List<Course> recommendCourse = courseRecommendService.getRecommendCourse(userId);
        List<Integer> systemRecommendIds = recommendCourse.stream().map(Course::getId).toList();
        List<Course> reCourseAns = courseRecommendService.recommendWithAdmin(recommendCourse, userId);
        Set<Integer> seen = new HashSet<>();
        List<Course> dedupedList = new ArrayList<>(reCourseAns.stream()
                .filter(course -> seen.add(course.getId()))
                .toList());
        List<Course> endList = courseRecommendService.sortByUserViewCount(dedupedList, userId);
        if (endList.isEmpty()) {
            return R.success(null, "系统也算不出你喜欢什么了", 500);
        }
        return R.success(endList)
                .add("systemRecommendIds", systemRecommendIds);
    }

    @ApiOperation("热力矩阵")
    @GetMapping("/getHeatMatrix")
    public R getHeatMatrix() {
        HashMap<String, Object> stringObjectHashMap = courseRecommendService.CovAdminEE();
        double[][] similarityMatrix = (double[][]) stringObjectHashMap.get("similarityMatrix");
        @SuppressWarnings("unchecked")
        List<Integer> users = (List<Integer>) stringObjectHashMap.get("users");
        return R.success(similarityMatrix)
                .add("users", users);
    }
}
