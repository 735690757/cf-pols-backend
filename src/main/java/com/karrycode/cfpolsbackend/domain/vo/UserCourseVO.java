package com.karrycode.cfpolsbackend.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/5/22 16:59
 * @PackageName com.karrycode.cfpolsbackend.domain.vo
 * @ClassName UserCourseVO
 * @Description
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCourseVO {
    private String cover;
    private String avatar;
    private String title;
    private String nickName;
    private Double score;
}
