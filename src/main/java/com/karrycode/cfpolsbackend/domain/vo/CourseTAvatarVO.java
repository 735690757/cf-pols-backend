package com.karrycode.cfpolsbackend.domain.vo;

import com.karrycode.cfpolsbackend.domain.po.Course;
import lombok.*;

import java.util.List;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/2/3 13:49
 * @PackageName com.karrycode.cfpolsbackend.domain.vo
 * @ClassName CourseTAvatarVO
 * @Description
 * @Version 1.0
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseTAvatarVO {
    /**
     * 课程
     */
    private List<Course> course;
    /**
     * 教师头像
     */
    private List<String> teacherAvatar;
}
