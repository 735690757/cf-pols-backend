package com.karrycode.cfpolsbackend.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/5/8 19:55
 * @PackageName com.karrycode.cfpolsbackend.domain.vo
 * @ClassName TeacherCourseVO
 * @Description
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherCourseVO {
    private String cover;
    private String title;
    private String status;
    private Integer viewCount;
    private Integer buyCount;
    private Double price;
    private Double avgScore;
}
