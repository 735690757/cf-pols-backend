package com.karrycode.cfpolsbackend.controller;

import com.karrycode.cfpolsbackend.common.R;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/2/17 16:40
 * @PackageName com.karrycode.cfpolsbackend.controller
 * @ClassName ProxyController
 * @Description
 * @Version 1.0
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/proxy")
public class ProxyController {
    /**
     *
     * @return R
     */
    @ApiOperation("获取壁纸代理服务")
    @GetMapping("/wallpaper")
    public R getWallpaper() {
        try {
            String urlP = "https://wp.upx8.com/api.php?content=风景";
            URL url = new URL(urlP);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET"); // 设置请求方法
            conn.setRequestProperty("User-Agent", "Mozilla/5.0"); // 设置请求头

            int responseCode = conn.getResponseCode();
            // 此处302
            if (responseCode == 302) {
                String location = conn.getHeaderField("Location");
                return R.success(location);
            }
        } catch (Exception e) {
            log.error("获取壁纸失败", e);
        }
        return R.error("获取壁纸失败");
    }
}