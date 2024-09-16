package com.example.serviceback.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.serviceback.service.ActivityUserService;
import com.example.serviceback.util.Result;
import com.example.serviceback.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户活动关系表 前端控制器
 * </p>
 *
 * @author CJW
 * @since 2024-06-30
 */
@RestController
@RequestMapping("/activityUser")
public class ActivityUserController {
    @Autowired
    private ActivityUserService activityUserService;

    @GetMapping("/isJoin/{userId}/{activityId}")
    public Result<Integer> isJoin(@PathVariable Long userId, @PathVariable Long activityId) {
        boolean join = activityUserService.isJoin(userId, activityId);
        return Result.success(join ? 1 : 0);
    }

    @GetMapping("/joinUser/{activityId}")
    public Result<Page<UserVO>> getUserList(@PathVariable Long activityId,
                                            @RequestParam Long currentPage, @RequestParam Long pageSize) {
        Page<UserVO> users = activityUserService.getUserList(activityId, currentPage, pageSize);
        return Result.success(users);
    }
}
