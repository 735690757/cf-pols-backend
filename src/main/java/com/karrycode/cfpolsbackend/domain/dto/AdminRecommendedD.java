package com.karrycode.cfpolsbackend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/5/10 12:43
 * @PackageName com.karrycode.cfpolsbackend.domain.dto
 * @ClassName AdminRecommendedD
 * @Description
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("AdminRecommendedD")
public class AdminRecommendedD {
    private Integer courseId;
}
