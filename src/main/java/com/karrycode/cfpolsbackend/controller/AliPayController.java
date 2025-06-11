package com.karrycode.cfpolsbackend.controller;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/2/5 18:15
 * @PackageName com.karrycode.cfpolsbackend.controller
 * @ClassName AliPayController
 * @Description
 * @Version 1.0
 */

import cn.dev33.satoken.stp.StpUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.karrycode.cfpolsbackend.common.R;
import com.karrycode.cfpolsbackend.config.AlipayTemplate;
import com.karrycode.cfpolsbackend.domain.po.OrderAli;
import com.karrycode.cfpolsbackend.domain.po.User;
import com.karrycode.cfpolsbackend.domain.vo.ExtOrder;
import com.karrycode.cfpolsbackend.service.OrderAliService;
import com.karrycode.cfpolsbackend.service.UserService;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付宝接口
 */
@CrossOrigin
@RestController
@RequestMapping("/alipay")
public class AliPayController {
    @Resource
    private AlipayTemplate alipayTemplate;
    @Resource
    private OrderAliService orderAliService;
    @Resource
    private UserService userService;
    private static final SecureRandom random = new SecureRandom();
    private static final String DIGITS = "0123456789";

    /**
     * 生成支付表单id
     *
     * @return 表单id
     */
    public static String generateFormId() {
        StringBuilder sb = new StringBuilder(11);
        for (int i = 0; i < 11; i++) {
            sb.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        return sb.toString();
    }

    /**
     * 支付触发
     *
     * @param id    id
     * @param money 金额
     * @return 交易结果
     * @throws AlipayApiException 支付宝接口异常
     */
    @ApiOperation("支付宝支付")
    @GetMapping(value = "/pay", produces = "text/html")
    @ResponseBody
    public String pay(@RequestParam long id, @RequestParam double money) throws AlipayApiException {
        // 随机生成表单id
        Long uuid = Long.valueOf(generateFormId());
        OrderAli orderAli = OrderAli.builder()
                .id(uuid)
                .userId(id)
                .money(money)
                .interfaceInfoId(id)
                .paymentMethod("支付宝")
                .build();
        System.out.println(orderAli);
        orderAliService.save(orderAli);
        return alipayTemplate.pay(orderAli);
    }

    /**
     * 支付宝异步回调
     *
     * @param request 请求
     * @return 回调结果
     * @throws Exception 异常
     */
    @ApiOperation("支付宝异步回调")
    @PostMapping("/notify")  // 注意这里必须是POST接口
    public String payNotify(HttpServletRequest request) throws Exception {
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            System.out.println("=========支付宝异步回调========");

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
                // System.out.println(name + " = " + request.getParameter(name));
            }

            String outTradeNo = params.get("out_trade_no");
            String gmtPayment = params.get("gmt_payment");
            String alipayTradeNo = params.get("trade_no");

            String sign = params.get("sign");
            String content = AlipaySignature.getSignCheckContentV1(params);
            boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, alipayTemplate.getAlipayPublicKey(), "UTF-8"); // 验证签名
            // 支付宝验签
            if (checkSignature) {
                // 验签通过
              /*  System.out.println("交易名称: " + params.get("subject"));
                System.out.println("交易状态: " + params.get("trade_status"));
                System.out.println("支付宝交易凭证号: " + params.get("trade_no"));
                System.out.println("商户订单号: " + params.get("out_trade_no"));
                System.out.println("交易金额: " + params.get("total_amount"));
                System.out.println("买家在支付宝唯一id: " + params.get("buyer_id"));
                System.out.println("买家付款时间: " + params.get("gmt_payment"));
                System.out.println("买家付款金额: " + params.get("buyer_pay_amount"));*/

                // 查询订单
                LambdaQueryWrapper<OrderAli> orderAliLambdaQueryWrapper = new LambdaQueryWrapper<>();
                orderAliLambdaQueryWrapper.eq(OrderAli::getId, outTradeNo);
                OrderAli orders = orderAliService.getOne(orderAliLambdaQueryWrapper);
                if (orders != null) {
                    orders.setStatus(1);
                    orderAliService.updateById(orders);
                    LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    userLambdaQueryWrapper.eq(User::getId, params.get("subject"));
                    User user = userService.getOne(userLambdaQueryWrapper);
                    if (params.get("buyer_pay_amount").equals("666.00")) {
                        user.setFund("-1");
                    } else {
                        user.setFund(String.valueOf(Double.parseDouble(user.getFund()) + 11 * Double.parseDouble(params.get("buyer_pay_amount"))));
                    }
                    userService.updateById(user);
                }
            }
        }
        return "success";
    }

    /**
     * 管理员获取所有订单
     *
     * @return R
     */
    @ApiOperation("管理员获取所有订单")
    @GetMapping("/getAllOrder")
    public R getAllOrder() {
        List<ExtOrder> orderAliList = orderAliService.listAll();
        Integer times = orderAliService.getNowDayCount();
        Double allMoney = orderAliService.getSumNowDay();
        return R.success(orderAliList)
                .add("times", times)
                .add("allMoney", allMoney == null ? 0 : allMoney);
    }

    /**
     * 获取自己的充值记录
     *
     * @return R
     */
    @ApiOperation("获取自己的充值记录")
    @GetMapping("/getMyOrder")
    public R getMyOrder() {
        String loginId = StpUtil.getLoginId().toString();
        return R.success(orderAliService.lambdaQuery().eq(OrderAli::getUserId, loginId).orderByDesc(OrderAli::getCreateTime).list());
    }
}
