package com.karrycode.cfpolsbackend.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.karrycode.cfpolsbackend.common.R;
import com.karrycode.cfpolsbackend.config.MinioInfo;
import com.karrycode.cfpolsbackend.domain.dto.UserLoginLogD;
import com.karrycode.cfpolsbackend.domain.eo.IdentityE;
import com.karrycode.cfpolsbackend.domain.po.User;
import com.karrycode.cfpolsbackend.domain.vo.DailyLogTime;
import com.karrycode.cfpolsbackend.domain.vo.PageUserVO;
import com.karrycode.cfpolsbackend.domain.vo.PageVO;
import com.karrycode.cfpolsbackend.domain.vo.TeacherInfoVO;
import com.karrycode.cfpolsbackend.service.UserService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * 用户表(User)控制层
 *
 * @author makejava
 * @since 2025-01-11 17:47:40
 */
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {
    /**
     * 服务对象
     */
    @Autowired
    private UserService userService;
    @Autowired
    private MinioInfo minioInfo;
    @Resource
    private MinioClient minioClient;
    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * @param userSTU 用户信息
     * @return R
     */
    @SaIgnore
    @ApiOperation("用户（学生）注册")
    @PostMapping("/register")
    public R register(@RequestBody User userSTU) {
        // 判断用户名是否重复
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getUserName, userSTU.getUserName());
        if (userService.getOne(userLambdaQueryWrapper) != null) {
            return R.error("用户名已存在");
        }
        // 设置身份为学生
        userSTU.setIdentity(IdentityE.STUDENT.name());
        boolean isSave = userService.save(userSTU);
        // 判断是否注册成功
        if (isSave) {
            return R.success(null, "注册成功", 200);
        } else {
            return R.error("注册失败");
        }
    }

    /**
     * @param user 用户信息
     * @return R
     */
    @SaIgnore
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public R login(@RequestBody User user) {
        // 判断用户名和密码是否匹配
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getUserName, user.getUserName())
                .eq(User::getPassword, user.getPassword());
        User loginUser = userService.getOne(userLambdaQueryWrapper);
        // 判断是否登录成功
        if (loginUser != null) {
            if (loginUser.getIsDisabled()) {
                return R.error("该账户已被封禁");
            }
            StpUtil.login(loginUser.getId());
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            // 写日志
            UserLoginLogD userLoginLogD = UserLoginLogD.builder()
                    .userId(loginUser.getId())
                    .userName(loginUser.getUserName())
                    .nickName(loginUser.getNickName())
                    .identity(IdentityE.valueOf(loginUser.getIdentity()))
                    .loginTime(DateUtil.now())
                    .build();
            mongoTemplate.insert(userLoginLogD, "userLoginLog");
            return R.success(loginUser, "登录成功", 200)
                    .add("token", tokenInfo.getTokenValue());
        } else {
            return R.error("用户名或密码不正确");
        }
    }

    /**
     * @return R
     */
    @ApiOperation("用户登录校验")
    @GetMapping("/checkLogin")
    public R checkLogin() {
        if (StpUtil.isLogin()) {
            return R.success(null, "已登录", 200);
        }
        return R.error("未登录", 401);
    }

    /**
     * @return R
     */
    @ApiOperation("用户登出")
    @GetMapping("/logout")
    public R logout() {
        StpUtil.logout();
        return R.success(null, "登出成功", 200);
    }

    /**
     * @return R
     */
    @ApiOperation("根据token获取当前用户信息")
    @GetMapping("/getCurrentUser")
    public R getCurrentUser() {
        User user = userService.getById(StpUtil.getLoginIdAsInt());
        return R.success(user, "获取成功", 200);
    }

    /**
     * @param user 用户
     * @return R
     */
    @ApiOperation("根据token更新用户信息")
    @PostMapping("/updateCurrentUser")
    public R updateCurrentUser(@RequestBody User user) {
        int loginIdAsInt = StpUtil.getLoginIdAsInt();
        user.setId(loginIdAsInt);
        user.setModifyTime(null);
        userService.updateById(user);
        return R.success(null, "更新成功", 200);
    }

    /**
     * @param avatar 头像文件
     * @return R
     * @throws Exception
     */
    @ApiOperation("上传用户头像")
    @PostMapping("/uploadAvatar")
    public R uploadAvatar(MultipartFile avatar) throws Exception {
        // 获取登录id、计算文件MD5、拼接文件名、上传到MinIO、更新用户信息
        int loginIdAsInt = StpUtil.getLoginIdAsInt();
        String fileExt = Objects.requireNonNull(avatar.getOriginalFilename()).
                substring(avatar.getOriginalFilename().lastIndexOf("."));
        String fileMd5 = SecureUtil.md5(avatar.getInputStream());
        String fileName = fileMd5 + fileExt;
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(minioInfo.getBucketName())
                .stream(avatar.getInputStream(), avatar.getInputStream().available(), -1)
                .object(fileName)
                .build();
        minioClient.putObject(putObjectArgs);
        User loginUser = userService.getById(loginIdAsInt);
        loginUser.setAvatar(fileName);
        boolean update = userService.updateById(loginUser);
        if (!update) {
            return R.error("上传失败");
        }
        return R.success(null, "上传成功", 200);
    }

    /**
     * @return R
     */
    @ApiOperation("获取用户登录日志")
    @GetMapping("/getUserLoginLog")
    public R<List<UserLoginLogD>> getUserLoginLog() {
        Sort loginTimeDesc = Sort.by(Sort.Direction.DESC, "loginTime");
        Query userLoginLogDQuery = new Query().with(loginTimeDesc).addCriteria(Criteria.where("userId").is(StpUtil.getLoginIdAsInt()));
        userLoginLogDQuery.limit(10);
        List<UserLoginLogD> userLoginLogDList = mongoTemplate.find(userLoginLogDQuery, UserLoginLogD.class, "userLoginLog");
        return R.success(userLoginLogDList, "获取成功", 200);
    }

    /**
     * @return R
     */
    @SaCheckRole("ADMIN")
    @ApiOperation("获取身份为管理员的所有用户")
    @GetMapping("/getAllAdminUser")
    public R<List<User>> getAllAdminUser() {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getIdentity, IdentityE.ADMIN.name());
        List<User> userList = userService.list(userLambdaQueryWrapper);
        return R.success(userList, "获取成功", 200);
    }

    /**
     * @param id        用户id
     * @param banStatus 封禁状态
     * @return R
     */
    @ApiOperation("根据用户id设置封禁值")
    @GetMapping("/switchBanStatus")
    public R banUserById(@RequestParam("id") Integer id, @RequestParam("banStatus") boolean banStatus) {
        User user = userService.getById(id);
        if (user == null) {
            return R.error("用户不存在");
        }
        if (banStatus) {
            user.setIsDisabled(true);
        } else {
            user.setIsDisabled(false);
        }
        user.setModifyTime(null);
        userService.updateById(user);
        return R.success(null, "成功", 200);
    }

    /**
     * @param user 用户
     * @return R
     */
    @ApiOperation("添加一个管理员")
    @PostMapping("/addAdminUser")
    public R addAdminUser(@RequestBody User user) {
        user.setIdentity(IdentityE.ADMIN.name());
        user.setIsDisabled(false);
        user.setFund("0");
        userService.save(user);
        return R.success(null, "成功", 200);
    }

    /**
     * @param id 用户id
     * @return R
     */
    @ApiOperation("删除一个用户")
    @GetMapping("/deleteUser")
    public R deleteUser(@RequestParam("id") Integer id) {
        userService.removeById(id);
        return R.success(null, "成功", 200);
    }

    /**
     * @param keyword 关键词
     * @return R
     */
    @ApiOperation("模糊搜索管理员用户")
    @GetMapping("/searchUser")
    public R<List<User>> searchUser(@RequestParam("keyword") String keyword) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getIdentity, IdentityE.ADMIN.name())
                .and(wrapper -> wrapper
                        .like(User::getUserName, keyword)
                        .or()
                        .like(User::getNickName, keyword));
        List<User> userList = userService.list(userLambdaQueryWrapper);
        return R.success(userList, "获取成功", 200);
    }

    /**
     * @return R
     */
    @SaCheckRole("ADMIN")
    @ApiOperation("获取身份为教师的用户")
    @GetMapping("/getAllTeacherUser")
    public R<List<User>> getAllTeacherUser() {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getIdentity, IdentityE.TEACHER.name());
        List<User> userList = userService.list(userLambdaQueryWrapper);
        return R.success(userList, "获取成功", 200);
    }

    /**
     * @param pageVO 页面值对象
     * @return R
     */
    @ApiOperation("分页获取身份为教师的用户")
    @PostMapping("/getAllTeacherUserByPage")
    public R<PageUserVO> getAllTeacherUserByPage(@RequestBody PageVO pageVO) {
        Long sizePage = pageVO.getSizePage();
        Long currentPage = pageVO.getCurrentPage();
        Page<User> teacherPage = new Page<>();
        teacherPage.setCurrent(currentPage);
        teacherPage.setSize(sizePage);
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getIdentity, IdentityE.TEACHER.name());
        Page<User> page = userService.page(teacherPage, userLambdaQueryWrapper);
        List<User> records = page.getRecords();
        long total = page.getTotal();

        PageUserVO pageUserVO = PageUserVO.builder()
                .userList(records)
                .totalCount(page.getTotal())
                .totalCount(total)
                .build();
        return R.success(pageUserVO, "获取成功", 200);
    }

    /**
     * @param user 用户
     * @return R
     */
    @ApiOperation("添加教师")
    @PostMapping("/addTeacherUser")
    public R addTeacherUser(@RequestBody User user) {
        user.setIdentity(IdentityE.TEACHER.name());
        user.setIsDisabled(false);
        user.setFund("0");
        userService.save(user);
        return R.success(null, "成功", 200);
    }

    /**
     * @param keyword 关键词
     * @return R
     */
    @ApiOperation("模糊搜索管理员用户")
    @GetMapping("/searchTeacherUser")
    public R<List<User>> searchTeacherUser(@RequestParam("keyword") String keyword) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getIdentity, IdentityE.TEACHER.name())
                .and(wrapper -> wrapper
                        .like(User::getUserName, keyword)
                        .or()
                        .like(User::getNickName, keyword));
        List<User> userList = userService.list(userLambdaQueryWrapper);
        return R.success(userList, "获取成功", 200);
    }

    /**
     * @return R
     */
    @ApiOperation("获取身份为学生的用户")
    @GetMapping("/getAllStudentUser")
    public R<List<User>> getAllStudentUser() {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getIdentity, IdentityE.STUDENT.name());
        List<User> userList = userService.list(userLambdaQueryWrapper);
        return R.success(userList, "获取成功", 200);
    }

    /**
     * @param pageVO 页面值对象
     * @return R
     */
    @ApiOperation("分页获取身份为学生的用户")
    @PostMapping("/getAllStudentUserByPage")
    public R<PageUserVO> getAllStudentUserByPage(@RequestBody PageVO pageVO) {
        Long sizePage = pageVO.getSizePage();
        Long currentPage = pageVO.getCurrentPage();
        Page<User> studentPage = new Page<>();
        studentPage.setCurrent(currentPage);
        studentPage.setSize(sizePage);
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getIdentity, IdentityE.STUDENT.name());
        Page<User> page = userService.page(studentPage, userLambdaQueryWrapper);
        List<User> records = page.getRecords();
        long total = page.getTotal();
        PageUserVO pageUserVO = PageUserVO.builder()
                .userList(records)
                .totalCount(page.getTotal())
                .totalCount(total)
                .build();
        return R.success(pageUserVO, "获取成功", 200);
    }

    /**
     * @param user 用户
     * @return R
     */
    @ApiOperation("添加学生")
    @PostMapping("/addStudentUser")
    public R addStudentUser(@RequestBody User user) {
        user.setIdentity(IdentityE.STUDENT.name());
        user.setIsDisabled(false);
        if (user.getFund() == null || user.getFund().isEmpty()) {
            user.setFund("0");
        }
        userService.save(user);
        return R.success(null, "成功", 200);
    }

    /**
     * @param keyword 关键词
     * @return R
     */
    @ApiOperation("模糊搜索学生用户")
    @GetMapping("/searchStudentUser")
    public R<List<User>> searchStudentUser(@RequestParam("keyword") String keyword) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getIdentity, IdentityE.STUDENT.name())
                .and(wrapper -> wrapper
                        .like(User::getUserName, keyword)
                        .or()
                        .like(User::getNickName, keyword));
        List<User> userList = userService.list(userLambdaQueryWrapper);
        return R.success(userList, "获取成功", 200);
    }

    /**
     * @param id 用户ID
     * @return R
     */
    @ApiOperation("根据ID获取用户头像")
    @GetMapping("/getUserAvatar")
    public R<String> getUserAvatar(@RequestParam("id") Integer id) {
        User user = userService.getById(id);
        String avatar = user.getAvatar();
        return R.success(avatar, "获取成功", 200);
    }

    /**
     * @return R
     */
    @ApiOperation("根据token获取fund")
    @GetMapping("/getFund")
    public R<String> getFund() {
        String userId = StpUtil.getLoginId().toString();
        User user = userService.getById(userId);
        double acc = userService.getAccSum(userId);
        String fund = user.getFund();
        return R.success(fund, "获取成功", 200)
                .add("acc", acc);
    }

    /**
     * @param id 用户ID
     * @return R
     */
    @ApiOperation("根据id获取用户头像")
    @GetMapping("/getUserAvatarById")
    public R<String> getUserAvatarById(@RequestParam("id") Integer id) {
        User user = userService.getById(id);
        String avatar = user.getAvatar();
        return R.success(avatar, "获取成功", 200);
    }

    /**
     * @return R
     */
    @ApiOperation("我是谁")
    @GetMapping("/whoami")
    public R whoami() {
        String userId = StpUtil.getLoginId().toString();
        User user = userService.getById(userId);
        return R.success(user, "获取成功", 200);
    }

    /**
     * @return R
     */
    @ApiOperation("从mongodb查出token持有者每天的登录次数")
    @GetMapping("/getUserLoginLogTimes")
    public R<ArrayList<DailyLogTime>> getUserLoginLogTimes() {
        ArrayList<DailyLogTime> dailyLogTimes = userService.getLogDateTimes();
        return R.success(dailyLogTimes, "获取成功", 200);
    }

    /**
     * @param username 用户名
     * @return R
     */
    @ApiOperation("根据用户username获取头像")
    @GetMapping("/getUserAvatarByUsername")
    public R<String> getUserAvatarByUsername(@RequestParam("username") String username) {
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUserName, username));
        String avatar = user.getAvatar();
        return R.success(avatar, "获取成功", 200);
    }

    /**
     * @return R
     */
    @ApiOperation("获取用户学习点数")
    @GetMapping("/getUserFund")
    public R<String> getUserFund() {
        String userId = StpUtil.getLoginId().toString();
        User user = userService.getById(userId);
        String fund = user.getFund();
        return R.success(fund, "获取成功", 200);
    }

    /**
     * @param id 用户ID
     * @return R
     */
    @ApiOperation("根据ID获取老师的头像和名称")
    @GetMapping("/getTeacherInfoById")
    public R<TeacherInfoVO> getTeacherInfoById(@RequestParam("id") Integer id) {
        User user = userService.getById(id);
        TeacherInfoVO teacherInfoVO = TeacherInfoVO.builder()
                .avatar(user.getAvatar())
                .nickName(user.getNickName())
                .build();
        return R.success(teacherInfoVO, "获取成功", 200);
    }

    @ApiOperation("获取教师的学习点数")
    @GetMapping("/getTeacherFund")
    public R<String> getTeacherFund(String teacherId) {
        User user = userService.getById(teacherId);
        String fund = user.getFund();
        return R.success(fund, "获取成功", 200);
    }

    @ApiOperation("获取系统用户成分")
    @GetMapping("/getUserRatio")
    public R getUserRatio() {
        HashMap<String, Integer> ratio = userService.getUserRatio();
        return R.success(ratio, "获取成功", 200);
    }
}
