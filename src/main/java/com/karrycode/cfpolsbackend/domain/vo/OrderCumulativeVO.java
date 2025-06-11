package com.karrycode.cfpolsbackend.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.models.auth.In;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/4/21 10:56
 * @PackageName com.karrycode.cfpolsbackend.domain.vo
 * @ClassName OrderCumulativeVO
 * @Description
 * @Version 1.0
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCumulativeVO {
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private Integer nowDayCount;
    private Integer cumulativeOrderCount;
}
