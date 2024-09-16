package com.example.serviceback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.serviceback.constant.ActivityConstant;
import com.example.serviceback.controller.FileController;
import com.example.serviceback.exception.MyErrorEnum;
import com.example.serviceback.exception.MyException;
import com.example.serviceback.mapper.ActivityMapper;
import com.example.serviceback.mapper.ActivityUserMapper;
import com.example.serviceback.mapper.CardMapper;
import com.example.serviceback.po.Activity;
import com.example.serviceback.po.ActivityUser;
import com.example.serviceback.po.Card;
import com.example.serviceback.service.ActivityService;
import com.example.serviceback.service.ActivityUserService;
import com.example.serviceback.util.RedisDistanceUtils;
import com.example.serviceback.validation.dto.ActivityDTO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 体育活动表 服务实现类
 * </p>
 *
 * @author CJW
 * @since 2024-06-30
 */
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {
    @Autowired
    private FileController fileController;
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private ActivityUserMapper activityUserMapper;
    @Autowired
    private RedissonClient redissonClient;
    @Value("${file.requestPrefix}")
    private String mediaRequestPrefix;
    @Autowired
    private ActivityUserService activityUserService;
    @Autowired
    private RedisDistanceUtils redisDistanceUtils;
    @Autowired
    private CardMapper cardMapper;

    @Override
    public String add(ActivityDTO activityDTO) {
        Activity activity = new Activity();
        BeanUtils.copyProperties(activityDTO, activity);
        String position = activityDTO.getLongitude() + " " + activityDTO.getLatitude();
        activity.setPosition(position);
        activityMapper.insert(activity);
        return activity.getId().toString();
    }

    @Override
    public void saveMedia(Long activityId, MultipartFile file) throws IOException {
        String fileName = fileController.upload(file);
        Activity activity = new Activity();
        activity.setId(activityId);
        activity.setPhoto(fileName);
        activityMapper.updateById(activity);
    }

    @Override
    public Page<Activity> getPage(Long currentPage, Long pageSize, Integer isFinish, Long userId) {
        List<Long> joinIds = activityUserMapper.getActivityIdByUserId(userId);
        Page<Activity> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Activity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Activity::getIsFinish, isFinish);
        queryWrapper.ne(Activity::getPublishUserId, userId);
        queryWrapper.notIn(joinIds.size() != 0, Activity::getId, joinIds);
        queryWrapper.orderByDesc(Activity::getCreateTime);
        Page<Activity> activityPage = activityMapper.selectPage(page, queryWrapper);
        activityPage.getRecords().forEach(activity -> activity.setPhoto(mediaRequestPrefix + activity.getPhoto()));
        return activityPage;
    }

    @Override
    public Activity getInfoById(Long id) {
        Activity activity = this.getById(id);
        activity.setPhoto(mediaRequestPrefix + activity.getPhoto());
        return activity;
    }

    @Override
    public void join(Long userId, Long activityId) {
        RLock lock = redissonClient.getLock(activityId.toString());
        try {
            lock.lock();
            //判断是否已经入
            boolean isJoin = activityUserService.isJoin(userId, activityId);
            if (isJoin) {
                throw new MyException(MyErrorEnum.REPEAT_JOIN_ACTIVITY);
            }
            //判断是否还有名额
            Activity activity = activityMapper.selectById(activityId);
            if (activity == null) {
                throw new MyException(MyErrorEnum.SERVICE_ERROR);
            }
            int restNum = activity.getTotalNum() - activity.getJoinNum();
            if (restNum == 0) {
                throw new MyException(MyErrorEnum.OUT_OT_ACTIVITY_NUMBER);
            }
            //更新名额数
            activity.setJoinNum(activity.getJoinNum() + 1);
            activityMapper.updateById(activity);
            //加入操作
            ActivityUser activityUser = new ActivityUser();
            activityUser.setUserId(userId);
            activityUser.setActivityId(activityId);
            activityUserService.save(activityUser);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Page<Activity> getListByUserId(Long userId, Integer category, Long status, Long currentPage, Long pageSize) {
        Integer isFinish = (status == ActivityConstant.Status.FINISH ? ActivityConstant.Status.FINISH : ActivityConstant.Status.PROCEED);
        Page<Activity> page = new Page<>(currentPage, pageSize);
        if (category == ActivityConstant.Category.PUBLISH) {
            getPublishListByUserId(userId, isFinish, page);
        } else {
            getJoinListByUserId(userId, isFinish, page);
        }
        page.getRecords().forEach(activity -> activity.setPhoto(mediaRequestPrefix + activity.getPhoto()));
        return page;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteById(Long id) {
        Activity activity = activityMapper.selectById(id);
        activityMapper.deleteById(id);
        //删除关联信息
        fileController.deleteFile(activity.getPhoto());
        redisDistanceUtils.deleteDistance(activity.getId());
        LambdaQueryWrapper<ActivityUser> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(ActivityUser::getActivityId, activity.getId());
        activityUserMapper.delete(queryWrapper1);
        LambdaQueryWrapper<Card> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(Card::getActivityId, activity.getId());
        cardMapper.delete(queryWrapper2);
    }

    private void getJoinListByUserId(Long userId, Integer isFinish, Page<Activity> page) {
        List<Long> activityIds = activityUserMapper.getActivityIdByUserId(userId);
        if (activityIds.size() != 0) {
            LambdaQueryWrapper<Activity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(Activity::getId, activityIds).eq(Activity::getIsFinish, isFinish).orderByDesc(Activity::getCreateTime);
            this.page(page, queryWrapper);
        }
    }

    private void getPublishListByUserId(Long userId, Integer isFinish, Page<Activity> page) {
        LambdaQueryWrapper<Activity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Activity::getPublishUserId, userId).eq(Activity::getIsFinish, isFinish).orderByDesc(Activity::getCreateTime);
        this.page(page, queryWrapper);
    }
}
