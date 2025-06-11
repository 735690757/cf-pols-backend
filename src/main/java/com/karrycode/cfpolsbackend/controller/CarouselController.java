package com.karrycode.cfpolsbackend.controller;



import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.karrycode.cfpolsbackend.common.R;
import com.karrycode.cfpolsbackend.config.MinioInfo;
import com.karrycode.cfpolsbackend.domain.po.Carousel;
import com.karrycode.cfpolsbackend.service.CarouselService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;


/**
 * 轮播图表(Carousel)控制层
 *
 * @author makejava
 * @since 2025-01-11 17:43:33
 */
@CrossOrigin
@RestController
@RequestMapping("/carousel")
public class CarouselController {
    /**
     * 服务对象
     */
    @Autowired
    private CarouselService carouselService;
    @Resource
    private MinioInfo minioInfo;
    @Resource
    private MinioClient minioClient;

    /**
     * 查询所有轮播图
     * @return 所有轮播图
     */
    @ApiOperation("查询所有轮播图")
    @GetMapping("/getAllCarousel")
    public Object getAllCarousel(){
        LambdaQueryWrapper<Carousel> carouselLambdaQueryWrapper = new LambdaQueryWrapper<>();
        carouselLambdaQueryWrapper
                .orderByDesc(Carousel::getSort)
                .eq(Carousel::getIsDelete, 0)
                .orderByDesc(Carousel::getSort);
        return carouselService.list(carouselLambdaQueryWrapper);
    }

    /**
     * 查询所有已经发布的轮播图
     * @return 所有已经发布的轮播图
     */
    @ApiOperation("查询所有已经发布的轮播图")
    @GetMapping("/getAllPublishCarousel")
    public R getAllPublishCarousel(){
        LambdaQueryWrapper<Carousel> carouselLambdaQueryWrapper = new LambdaQueryWrapper<>();
        carouselLambdaQueryWrapper
                .eq(Carousel::getStatus, 1)
                .eq(Carousel::getIsDelete, 0)
                .orderByDesc(Carousel::getSort);
        return R.success(carouselService.list(carouselLambdaQueryWrapper));
    }

    /**
     * 根据轮播图id修改发布状态
     * @param id 零轮播图id
     * @param status 发布状态
     * @return R
     */
    @ApiOperation("根据轮播图id修改发布状态")
    @GetMapping("/updateCarouselStatus")
    public R updateCarouselStatus(Integer id, Integer status){
        Carousel carousel = new Carousel();
        carousel.setId(id);
        carousel.setStatus(status);
        return R.success(carouselService.updateById(carousel));
    }

    /**
     * 根据轮播图id设置删除标志
     * @param id 轮播图id
     * @return R
     */
    @ApiOperation("根据轮播图id设置删除标志")
    @GetMapping("/deleteCarousel")
    public R deleteCarousel(Integer id){
        Carousel carousel = new Carousel();
        carousel.setId(id);
        carousel.setIsDelete(1);
        return R.success(carouselService.updateById(carousel));
    }

    /**
     * 根据轮播图id修改排序字段
     * @param id 轮播图id
     * @param sort 排序字段
     * @return R
     */
    @ApiOperation("根据轮播图id修改排序字段")
    @GetMapping("/updateCarouselSort")
    public R updateCarouselSort(Integer id, Integer sort){
        Carousel carousel = new Carousel();
        carousel.setId(id);
        carousel.setSort(sort);
        return R.success(carouselService.updateById(carousel));
    }

    /**
     * 上传图像
     * @param file 文件
     * @return R
     * @throws Exception 上传异常
     */
    @ApiOperation("上传图像至minio")
    @PostMapping("/uploadImage")
    public R uploadImage(MultipartFile file) throws Exception {
        String fileExt = Objects.requireNonNull(file.getOriginalFilename()).
                substring(file.getOriginalFilename().lastIndexOf("."));
        String fileMd5 = SecureUtil.md5(file.getInputStream());
        String fileName = fileMd5 + fileExt;

        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(minioInfo.getBucketName())
                .stream(file.getInputStream(), file.getInputStream().available(), -1)
                .object(fileName)
                .build();
        minioClient.putObject(putObjectArgs);
        return R.success(fileName);
    }

    /**
     * 添加轮播图
     * @param carousel 轮播图
     * @return R
     */
    @ApiOperation("添加轮播图")
    @PostMapping("/addCarousel")
    public R addCarousel(@RequestBody Carousel carousel){
        return R.success(carouselService.save(carousel));
    }
}
