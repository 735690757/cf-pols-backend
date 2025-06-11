package com.karrycode.cfpolsbackend.controller;

import com.karrycode.cfpolsbackend.common.MyDbProperties;
import com.karrycode.cfpolsbackend.common.R;
import com.karrycode.cfpolsbackend.domain.dto.SystemSettingD;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/4/18 15:14
 * @PackageName com.karrycode.cfpolsbackend.controller
 * @ClassName SystemSettingController
 * @Description
 * @Version 1.0
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/setting")
public class SystemSettingController {
    @Autowired
    private MyDbProperties dbProps;
    @Autowired
    private MongoTemplate mongoTemplate;
    private final SystemInfo systemInfo = new SystemInfo();
    private final HardwareAbstractionLayer hardware = systemInfo.getHardware();
    private final CentralProcessor processor = hardware.getProcessor();

    @ApiOperation("更新系统设置")
    @PostMapping("/update")
    public R updateSystemSetting(@RequestBody SystemSettingD systemSettingD) {
        List<SystemSettingD> systemSettingDS = mongoTemplate.find(new Query(), SystemSettingD.class);
        mongoTemplate.remove(new Query(), "SystemSetting");
        systemSettingDS.get(0).setRate(systemSettingD.getRate());
        return R.success(mongoTemplate.insert(systemSettingDS, "SystemSetting"));
    }

    @ApiOperation("获取系统设置")
    @GetMapping("/get")
    public R getSystemSetting() {
        return R.success(mongoTemplate.findAll(SystemSettingD.class, "SystemSetting"));
    }

    @ApiOperation("实时获取CPU占用")
    @GetMapping("/getCpuUsage")
    public R getCpuUsage() throws InterruptedException {
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        TimeUnit.SECONDS.sleep(1); // 等待1秒
        long[] ticks = processor.getSystemCpuLoadTicks();
        double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks);
        double usagePercent = cpuLoad * 100;
        return R.success("ok")
                .add("cpuUsage", usagePercent);
    }

    @ApiOperation("获取本应用的系统内存占用")
    @GetMapping("/getMemoryUsage")
    public R getMemoryUsage() {
        long totalMemory = hardware.getMemory().getTotal();
        long usedMemory = totalMemory - hardware.getMemory().getAvailable();
        double memoryUsage = (double) usedMemory / totalMemory * 100;
        return R.success("ok")
                .add("memoryUsage", memoryUsage);
    }

    @ApiOperation("备份数据库")
    @GetMapping("/backup")
    public void backup(HttpServletResponse response) throws IOException {
        String host = dbProps.getHost();
        String user = dbProps.getUser();
        String password = dbProps.getPassword();
        String dbName = "cf-pols-db";

        // 设置响应头，告诉浏览器是个文件下载
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + dbName + "_backup.sql");

        // 构建 mysqldump 命令
        ProcessBuilder pb = new ProcessBuilder(
                "mysqldump", "-h" + host, "-u" + user, "-p" + password, dbName
        );

        // 启动进程
        Process process = pb.start();

        // 读取 mysqldump 的输出，并写入响应流
        try (
                InputStream inputStream = process.getInputStream();
                OutputStream outputStream = response.getOutputStream()
        ) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            List<SystemSettingD> systemSettingDS = mongoTemplate.find(new Query(), SystemSettingD.class);
            mongoTemplate.remove(new Query(), "SystemSetting");
            systemSettingDS.get(0).setDate(SimpleDateFormat.getDateInstance().format(new Date()));
            mongoTemplate.insert(systemSettingDS, "SystemSetting");
        } catch (IOException e) {
            throw new RuntimeException("备份失败", e);
        }
    }
}
