package com.example.serviceback.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.serviceback.exception.MyErrorEnum;
import com.example.serviceback.exception.MyException;
import com.example.serviceback.mapper.ActivityMapper;
import com.example.serviceback.mapper.CardMapper;
import com.example.serviceback.po.Activity;
import com.example.serviceback.po.Card;
import com.example.serviceback.service.CardService;
import com.example.serviceback.util.RedisDistanceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 打卡记录表 服务实现类
 * </p>
 *
 * @author CJW
 * @since 2024-06-30
 */
@Service
public class CardServiceImpl extends ServiceImpl<CardMapper, Card> implements CardService {
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private RedisDistanceUtils redisDistanceUtils;
    @Autowired
    private CardMapper cardMapper;

    @Override
    public void doCard(Long activityId, Long userId, Double longitude, Double latitude) {
        //先判断活动当前是否可以打卡
        Activity activity = activityMapper.selectById(activityId);
        if (activity.getIsFinish() != 0 || activity.getIsCard() != 1) {
            throw new MyException(MyErrorEnum.ACTIVITY_NOY_IN_CARD);
        }
        if (isInRange(activityId, userId, longitude, latitude)) {
            //进行打卡
            Card card = new Card();
            card.setActivityId(activityId);
            card.setUserId(userId);
            cardMapper.insert(card);
        } else {
            throw new MyException(MyErrorEnum.CARD_NOT_IN_RANGE);
        }
    }

    private boolean isInRange(Long activityId, Long userId, double longitude, double latitude) {
        redisDistanceUtils.addUserCardPosition(activityId, userId, longitude, latitude);
        double distance = redisDistanceUtils.distanceBetween(activityId, userId);
        return distance <= 100;
    }
}
