package com.karrycode.cfpolsbackend.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/5/9 13:58
 * @PackageName com.karrycode.cfpolsbackend.domain.vo
 * @ClassName SysInnerOrder
 * @Description
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysInnerOrder {
    private String title;
    private String avatar;
    private String nickName;
    private Integer amount;
    private Integer score;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String createTime;
}
