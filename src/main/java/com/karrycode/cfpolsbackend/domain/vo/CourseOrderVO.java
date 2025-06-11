package com.karrycode.cfpolsbackend.domain.vo;

import lombok.*;

import java.util.Date;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/4/18 16:22
 * @PackageName com.karrycode.cfpolsbackend.domain.vo
 * @ClassName CourseOrderListVO
 * @Description
 * @Version 1.0
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseOrderVO {
    private String title;
    private String amount;
    private String createTime;
}
