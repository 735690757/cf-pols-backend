package com.karrycode.cfpolsbackend.controller;


import com.karrycode.cfpolsbackend.domain.dto.InteractionD;
import com.karrycode.cfpolsbackend.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 交互表(Interaction)控制层
 *
 * @author makejava
 * @since 2025-01-11 17:45:43
 */
@CrossOrigin
@RestController
@RequestMapping("/interaction")
public class InteractionController {
    /**
     * 服务对象
     */
    @Autowired
    private InteractionService interactionService;

    /**
     * 交互统计
     * @param interactionD 交互对象
     */
    @PostMapping("/interactionACT")
    public void interaction(@RequestBody InteractionD interactionD) {
        interactionService.interaction(interactionD.getUserId(), interactionD.getCourseId());
    }
}

