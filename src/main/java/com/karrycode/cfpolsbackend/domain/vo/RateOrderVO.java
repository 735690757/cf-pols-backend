package com.karrycode.cfpolsbackend.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/4/8 13:12
 * @PackageName com.karrycode.cfpolsbackend.domain.vo
 * @ClassName RateOrderVO
 * @Description
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RateOrderVO {
    private Integer userId;
    private Integer courseId;
    private Double rate;
}
