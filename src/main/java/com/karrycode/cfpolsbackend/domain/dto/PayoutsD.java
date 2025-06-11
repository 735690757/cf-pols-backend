package com.karrycode.cfpolsbackend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/4/18 20:29
 * @PackageName com.karrycode.cfpolsbackend.domain.dto
 * @ClassName PayoutsD
 * @Description
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("Payouts")
public class PayoutsD {
    private String teacherID;
    private String teacherName;
    private Double learnCount;
    private Double payout;
    private Double payoutRate;
    private boolean isPaid;
}
