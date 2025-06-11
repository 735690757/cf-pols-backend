package com.karrycode.cfpolsbackend.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.karrycode.cfpolsbackend.domain.po.OrderAli;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/2/5 18:02
 * @PackageName com.karrycode.cfpolsbackend.config
 * @ClassName AlipayTemplate
 * @Description
 * @Version 1.0
 */
@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {
    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    @Value("{alipay.appId}")
    public String appId;
    // 应用私钥，就是工具生成的应用私钥
    @Value("{alipay.merchantPrivateKey}")
    public String merchantPrivateKey;
    // 支付宝公钥,对应APPID下的支付宝公钥。
    @Value("{alipay.alipayPublicKey}")
    public String alipayPublicKey;
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    @Value("{alipay.notifyUrl}")
    public String notifyUrl;
    // 同步通知，支付成功，一般跳转到成功页
    @Value("{alipay.returnUrl}")
    public String returnUrl;
    // 签名方式
    @Value("{alipay.signType}")
    private String signType;
    // 字符编码格式
    @Value("{alipay.charset}")
    private String charset;
    // 订单超时时间
    private String timeout = "5m";
    // 支付宝网关；https://openapi-sandbox.dl.alipaydev.com/gateway.do
    @Value("{alipay.gatewayUrl}")
    public String gatewayUrl;

    public String pay(OrderAli order) throws AlipayApiException {
        // 1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new
                DefaultAlipayClient(gatewayUrl, appId, merchantPrivateKey,
                "json", charset, alipayPublicKey, signType);
        // 2、创建一个支付请求，并设置请求参数
        AlipayTradePagePayRequest alipayRequest = new
                AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(returnUrl);
        alipayRequest.setNotifyUrl(notifyUrl);
        Long id = order.getId();
        Long interfaceInfoId = order.getInterfaceInfoId();
        Double money = order.getMoney();
        String paymentMethod = order.getPaymentMethod();
        alipayRequest.setBizContent(" {\"out_trade_no\":\"" + id + "\","
                + "\"total_amount\":\"" + money + "\","
                + "\"subject\":\"" + interfaceInfoId
                + "\","
                + "\"body\":\"" + paymentMethod + "\","
                +
                "\"timeout_express\":\"" + timeout + "\","
                +
                "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        // 会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        return alipayClient.pageExecute(alipayRequest).getBody();
    }
}