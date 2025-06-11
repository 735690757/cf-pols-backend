package com.karrycode.cfpolsbackend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/4/18 11:52
 * @PackageName com.karrycode.cfpolsbackend.domain.dto
 * @ClassName SystemSettingD
 * @Description
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("SystemSetting")
public class SystemSettingD {
    private Integer rate;
    private String date;
}
