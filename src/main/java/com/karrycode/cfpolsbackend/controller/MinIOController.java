package com.karrycode.cfpolsbackend.controller;

import com.karrycode.cfpolsbackend.common.R;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/1/12 14:52
 * @PackageName com.karrycode.cfpolsbackend.controller
 * @ClassName MinIOController
 * @Description
 * @Version 1.0
 */
@CrossOrigin
@RestController
@RequestMapping("/minio")
public class MinIOController {
    @Resource
    private MinioClient minioClient;

    /**
     *
     * @return R
     */
    @GetMapping("/bannerVideo")
    public R<String> getBannerVideo() {
        return R.success("LoginV.mp4");
    }

    /**
     *
     * @param filename 文件名
     * @return 访问链接
     * @throws Exception IO异常
     */
    @ApiOperation("生成视频访问链接")
    @GetMapping("/get-url")
    public String getVideoUrl(@RequestParam String filename) throws Exception {
        // 生成一个 1 小时有效的 Presigned URL
        String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(io.minio.http.Method.GET)
                        .bucket("cf-pols-minio") // 你的桶名
                        .object(filename)
                        .expiry(60 * 60 * 60)
                        .build()
        );
        return url;
    }

    /**
     *
     * @return 全量URL
     */
    @ApiOperation("获取宿主机IP所拼接的全量URL")
    @GetMapping("/getHostIp")
    public String getHostIp() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return "http://"+inetAddress.getHostAddress()+":9090/cf-pols-minio/";
        } catch (UnknownHostException e) {
            return "Error: Unable to get host IP";
        }
    }
}
