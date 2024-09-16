package com.example.serviceback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.serviceback.po.Card;

/**
 * <p>
 * 打卡记录表 服务类
 * </p>
 *
 * @author CJW
 * @since 2024-06-30
 */
public interface CardService extends IService<Card> {

    /**
     * 打卡
     * @param activityId
     * @param userId
     * @param longitude
     * @param latitude
     */
    void doCard(Long activityId, Long userId, Double longitude, Double latitude);
}
