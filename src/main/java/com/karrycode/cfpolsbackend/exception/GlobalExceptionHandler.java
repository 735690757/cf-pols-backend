package com.karrycode.cfpolsbackend.exception;

import com.karrycode.cfpolsbackend.common.R;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2024/1/12 下午 1:01
 * @PackageName edu.karryCode.exception
 * @ClassName GlobalExceptionHandler
 * @Description 全局异常处理
 * @Version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        e.printStackTrace();
        return R.error(StringUtils.hasLength(e.getMessage())?e.getMessage():"系统错误");
    }
}
