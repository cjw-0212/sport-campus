package com.example.serviceback.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.serviceback.po.ActivityUser;
import com.example.serviceback.vo.UserVO;

/**
 * <p>
 * 用户活动关系表 服务类
 * </p>
 *
 * @author CJW
 * @since 2024-06-30
 */
public interface ActivityUserService extends IService<ActivityUser> {
    /**
     * 判断是否已加入该活动
     *
     * @param userId
     * @param activityId
     * @return
     */
    boolean isJoin(Long userId, Long activityId);

    /**
     * 获取活动参加人员
     *
     * @param activityId
     * @return
     */
    Page<UserVO> getUserList(Long activityId, Long currentPage, Long pageSize);
}
