package com.karrycode.cfpolsbackend.domain.vo;

import lombok.*;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/5/9 21:46
 * @PackageName com.karrycode.cfpolsbackend.domain.vo
 * @ClassName ExtOrder
 * @Description
 * @Version 1.0
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExtOrder {
    private String oid;
    private String nickName;
    private String money;
    private String paymentMethod;
    private String status;
    private String createTime;
}
