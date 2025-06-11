package com.karrycode.cfpolsbackend.domain.dto;

import lombok.*;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/4/1 19:31
 * @PackageName com.karrycode.cfpolsbackend.domain.dto
 * @ClassName InteractionD
 * @Description
 * @Version 1.0
 */
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InteractionD {
    private String userId;
    private String courseId;
}
