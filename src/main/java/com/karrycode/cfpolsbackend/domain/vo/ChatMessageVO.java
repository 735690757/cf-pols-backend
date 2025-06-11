package com.karrycode.cfpolsbackend.domain.vo;

import lombok.*;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/2/10 13:14
 * @PackageName com.karrycode.cfpolsbackend.domain.vo
 * @ClassName ChatMessage
 * @Description
 * @Version 1.0
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageVO {
    private String role;    // "user" 或 "assistant"
    private String content; // 消息内容
}
