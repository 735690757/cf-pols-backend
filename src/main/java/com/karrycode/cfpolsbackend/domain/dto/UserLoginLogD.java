package com.karrycode.cfpolsbackend.domain.dto;

import com.karrycode.cfpolsbackend.domain.eo.IdentityE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/1/14 21:57
 * @PackageName com.karrycode.cfpolsbackend.domain.dto
 * @ClassName UserLoginLogD
 * @Description
 * @Version 1.0
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("userLoginLog")
public class UserLoginLogD {
    private Integer userId;
    private String userName;
    private String nickName;
    private IdentityE identity;
    private String loginTime;
}
