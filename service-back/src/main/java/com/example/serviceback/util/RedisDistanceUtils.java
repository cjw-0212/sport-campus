package com.example.serviceback.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author CJW
 * @since 2024/8/21
 */
@Component
public class RedisDistanceUtils {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 作为存储经纬度列表的key值
     */
    private static final String GEO_KEY = "distance:";
    private static final String CENTER = "center";

    /**
     * 将活动打卡的中心位置加入redis
     *
     * @param activityId
     * @param longitude
     * @param latitude
     */
    public void setActivityCenterPosition(Long activityId, double longitude, double latitude) {
        GeoOperations geoOperations = stringRedisTemplate.opsForGeo();
        Point point = new Point(longitude, latitude);
        RedisGeoCommands.GeoLocation geoLocation = new RedisGeoCommands.GeoLocation(CENTER, point);
        geoOperations.add(GEO_KEY + activityId.toString(), geoLocation);
    }

    /**
     * 将用户打卡位置加入对应的活动的key里
     *
     * @param userId
     * @param activityId
     * @param longitude
     * @param latitude
     */
    public void addUserCardPosition(Long activityId, Long userId, double longitude, double latitude) {
        GeoOperations geoOperations = stringRedisTemplate.opsForGeo();
        Point point = new Point(longitude, latitude);
        RedisGeoCommands.GeoLocation geoLocation = new RedisGeoCommands.GeoLocation(userId.toString(), point);
        geoOperations.add(GEO_KEY + activityId.toString(), geoLocation);
    }


    /**
     * 用户位置与活动打卡位置的距离
     */
    public double distanceBetween(Long activityId, Long userId) {
        GeoOperations geoOperations = stringRedisTemplate.opsForGeo();
        Distance distance = geoOperations.distance(GEO_KEY + activityId, CENTER, userId.toString());
        return distance.getValue();
    }

    public void deleteDistance(Long activityId) {
        stringRedisTemplate.delete(GEO_KEY + activityId.toString());
    }
}
