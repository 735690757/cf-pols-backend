package com.karrycode.cfpolsbackend.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/5/22 14:48
 * @PackageName com.karrycode.cfpolsbackend.domain.vo
 * @ClassName UccVO
 * @Description
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UccVO {
    private int userId;
    private int courseId;
    private Double score;
}
