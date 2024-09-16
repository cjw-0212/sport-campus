package com.example.serviceback.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.serviceback.po.Activity;
import com.example.serviceback.service.ActivityService;
import com.example.serviceback.util.RedisDistanceUtils;
import com.example.serviceback.util.Result;
import com.example.serviceback.validation.dto.ActivityDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>
 * 体育活动表 前端控制器
 * </p>
 *
 * @author CJW
 * @since 2024-06-30
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private RedisDistanceUtils redisDistanceUtils;

    @GetMapping("/info/{id}")
    public Result<Activity> getInfoById(@PathVariable Long id) {
        Activity activity = activityService.getInfoById(id);
        return Result.success(activity);
    }

    @PostMapping("/publish/text")
    public Result<String> publishText(@Validated @RequestBody ActivityDTO activityDTO) {
        String activityId = activityService.add(activityDTO);
        return Result.success(activityId);
    }

    @PostMapping("/publish/media/{activityId}")
    public Result<Void> publishMedia(@PathVariable Long activityId, MultipartFile file) throws IOException {
        activityService.saveMedia(activityId, file);
        return Result.success();
    }

    @GetMapping("/page/{userId}/{isFinish}")
    public Result<Page<Activity>> page(@RequestParam Long currentPage, @RequestParam Long pageSize,
                                       @PathVariable Integer isFinish,@PathVariable Long userId) {
        Page<Activity> activityPage = activityService.getPage(currentPage, pageSize, isFinish,userId);
        return Result.success(activityPage);
    }

    @PostMapping("/join/{userId}/{activityId}")
    public Result<Void> join(@PathVariable Long userId, @PathVariable Long activityId) {
        activityService.join(userId, activityId);
        return Result.success();
    }

    @GetMapping("/{userId}/{category}/{status}")
    public Result<Page<Activity>> getPageByUserId(@PathVariable Long userId, @PathVariable Integer category,
                                                  @PathVariable Long status, @RequestParam Long currentPage,
                                                  @RequestParam Long pageSize) {
        Page<Activity> activityPage = activityService.getListByUserId(userId, category, status, currentPage, pageSize);
        return Result.success(activityPage);
    }

    @PutMapping("/{activityId}/openCard")
    public Result<Void> openCard(@PathVariable Long activityId, @RequestParam Double longitude,
                                 @RequestParam Double latitude) {
        Activity activity = new Activity();
        activity.setId(activityId);
        activity.setIsCard(1);
        activity.setPosition(longitude.toString() + " " + latitude.toString());
        activityService.updateById(activity);
        //将互动的中心位置存入redis
        redisDistanceUtils.setActivityCenterPosition(activityId, longitude, latitude);
        return Result.success();
    }

    @PutMapping("/{activityId}/finish")
    public Result<Void> finish(@PathVariable Long activityId) {
        Activity activity = new Activity();
        activity.setId(activityId);
        activity.setIsFinish(1);
        activityService.updateById(activity);
        //删除redis的打卡位置信息
        redisDistanceUtils.deleteDistance(activityId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        activityService.deleteById(id);
        return Result.success();
    }
}
