package com.karrycode.cfpolsbackend.domain.vo;

import lombok.*;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/2/16 18:33
 * @PackageName com.karrycode.cfpolsbackend.domain.vo
 * @ClassName DailyLogTime
 * @Description
 * @Version 1.0
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyLogTime {
    private String date;
    private int time;
}
