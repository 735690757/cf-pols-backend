package com.karrycode.cfpolsbackend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/4/8 09:50
 * @PackageName com.karrycode.cfpolsbackend.domain.dto
 * @ClassName LearningProgressD
 * @Description
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("learningProgress")
public class LearningProgressD {
    private Integer userId;
    private Integer courseId;
    private Integer nowSection;
    private Integer allSection;
    private Double percent;
}
