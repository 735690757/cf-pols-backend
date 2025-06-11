package com.karrycode.cfpolsbackend.controller;


import com.karrycode.cfpolsbackend.common.R;
import com.karrycode.cfpolsbackend.domain.po.Payout;
import com.karrycode.cfpolsbackend.domain.po.User;
import com.karrycode.cfpolsbackend.service.PayoutService;
import com.karrycode.cfpolsbackend.service.UserService;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 提现申请表(Payout)控制层
 *
 * @author makejava
 * @since 2025-04-18 20:52:24
 */
@CrossOrigin
@RestController
@RequestMapping("/payout")
public class PayoutController {
    /**
     * 服务对象
     */
    @Autowired
    private PayoutService payoutService;
    @Resource
    private UserService userService;

    @ApiOperation("添加提现申请")
    @PostMapping("/addPayout")
    public R addPayout(@RequestBody Payout payout) {
        User teacher = userService.getById(payout.getUserId());
        teacher.setFund(String.valueOf(Double.parseDouble(teacher.getFund()) - Double.parseDouble(payout.getLearnCount())));
        userService.updateById(teacher);
        return payoutService.save(payout) ? R.success("添加成功") : R.error("添加失败");
    }

    @ApiOperation("获取指定教师的提现记录")
    @GetMapping("/getTeacherPayout")
    public R getTeacherPayout(@RequestParam Integer teacherId) {
        return R.success(payoutService.lambdaQuery().eq(Payout::getUserId, teacherId).list());
    }

    @ApiOperation("获取提现记录")
    @GetMapping("/getPayout")
    public R getPayout() {
        return R.success(payoutService.lambdaQuery()
                .orderByAsc(Payout::getIspaid).list());
    }

    @ApiOperation("同意提现")
    @GetMapping("/agreePayout")
    public R agreePayout(@RequestParam Integer payoutId) {
        Payout payout = payoutService.getById(payoutId);
        payout.setIspaid(1);
        return payoutService.updateById(payout) ? R.success("操作成功") : R.error("操作失败");
    }

    @ApiOperation("提示成功")
    @GetMapping("/successPayout")
    public R successPayout(@RequestParam Integer payoutId) {
        Payout payout = payoutService.getById(payoutId);
        payout.setIspaid(2);
        return payoutService.updateById(payout) ? R.success("操作成功") : R.error("操作失败");
    }
}
