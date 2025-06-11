package com.karrycode.cfpolsbackend.permission;

import cn.dev33.satoken.stp.StpInterface;
import com.karrycode.cfpolsbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/1/12 15:50
 * @PackageName com.karrycode.cfpolsbackend.permission
 * @ClassName StpInterfaceImpl
 * @Description 权限接口实现类
 * @Version 1.0
 */
@Component
public class StpInterfaceImpl implements StpInterface {
    @Autowired
    private UserService userService;

    @Override
    public List<String> getPermissionList(Object o, String s) {
        return List.of();
    }

    @Override
    public List<String> getRoleList(Object id, String s) {
        Integer userID = Integer.parseInt(id.toString());
        String identity = userService.getById(userID).getIdentity();
        return switch (identity) {
            case "ADMIN" -> List.of("*");
            case "TEACHER" -> List.of("TEACHER");
            case "STUDENT" -> List.of("STUDENT");
            default -> List.of();
        };
    }
}
