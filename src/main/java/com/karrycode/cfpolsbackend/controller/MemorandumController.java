package com.karrycode.cfpolsbackend.controller;


import com.karrycode.cfpolsbackend.common.R;
import com.karrycode.cfpolsbackend.domain.po.Memorandum;
import com.karrycode.cfpolsbackend.service.MemorandumService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 备忘录表(Memorandum)控制层
 *
 * @author makejava
 * @since 2025-01-11 17:46:02
 */
@CrossOrigin
@RestController
@RequestMapping("/memorandum")
public class MemorandumController {
    /**
     * 服务对象
     */
    @Autowired
    private MemorandumService memorandumService;

    /**
     * @param memorandum 实体对象
     * @return R
     */
    @ApiOperation("添加备忘录速记")
    @PostMapping("/add")
    public R addMemorandum(@RequestBody Memorandum memorandum) {
        return R.success(memorandumService.save(memorandum));
    }

    /**
     * @param userId 用户id
     * @return R
     */
    @ApiOperation("获取用户所有备忘录")
    @GetMapping("/get/{userId}")
    public R getMemorandum(@PathVariable Integer userId) {
        return R.success(memorandumService
                .lambdaQuery()
                .eq(Memorandum::getUserId, userId)
                .orderByDesc(Memorandum::getCreateTime)
                .eq(Memorandum::getIsDelete, 0)
                .list());
    }

    @ApiOperation("删除备忘录")
    @DeleteMapping("/delete/{id}")
    public R deleteMemorandum(@PathVariable Integer id) {
        return R.success(memorandumService.lambdaUpdate()
                .eq(Memorandum::getId, id)
                .set(Memorandum::getIsDelete, 1)
                .update());
    }


}
