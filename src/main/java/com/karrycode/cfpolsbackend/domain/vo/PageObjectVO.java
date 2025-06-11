package com.karrycode.cfpolsbackend.domain.vo;

import lombok.*;

import java.util.List;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/5/11 11:16
 * @PackageName com.karrycode.cfpolsbackend.domain.vo
 * @ClassName PageObjectVO
 * @Description
 * @Version 1.0
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageObjectVO<T> {
    /**
     * 总数
     */
    private Long totalCount;
    /**
     * 对象列表
     */
    private List<T> objectList;
}
