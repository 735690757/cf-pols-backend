package com.karrycode.cfpolsbackend.domain.vo;

import com.karrycode.cfpolsbackend.domain.po.User;
import lombok.*;

import java.util.List;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/1/16 00:21
 * @PackageName com.karrycode.cfpolsbackend.domain.vo
 * @ClassName PageUserVO
 * @Description
 * @Version 1.0
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageUserVO {
    /**
     * 总数
     */
    private Long totalCount;
    /**
     * 用户列表
     */
    private List<User> userList;
}
