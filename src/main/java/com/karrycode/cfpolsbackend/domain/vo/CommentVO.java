package com.karrycode.cfpolsbackend.domain.vo;

import lombok.*;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/4/7 08:58
 * @PackageName com.karrycode.cfpolsbackend.domain.vo
 * @ClassName CommentVO
 * @Description
 * @Version 1.0
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {
    private Integer uid;
    private Integer uname;
    private Integer reid;
    private Integer rename;
    private Integer cid;
    private String content;
}
