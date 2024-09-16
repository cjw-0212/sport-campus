package com.example.serviceback.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.serviceback.po.Activity;
import com.example.serviceback.validation.dto.ActivityDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>
 * 体育活动表 服务类
 * </p>
 *
 * @author CJW
 * @since 2024-06-30
 */
public interface ActivityService extends IService<Activity> {
    /**
     * 新增活动
     *
     * @param activityDTO
     * @return
     */
    String add(ActivityDTO activityDTO);

    /**
     * 保存活动封面信息
     *
     * @param activityId
     * @param file
     */
    void saveMedia(Long activityId, MultipartFile file) throws IOException;

    /**
     * 获取活动列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页大小
     * @param isFinish    是否结束
     */
    Page<Activity> getPage(Long currentPage, Long pageSize, Integer isFinish,Long userId);

    /**
     * 根据id获取活动详详细信息
     *
     * @param id
     * @return
     */
    Activity getInfoById(Long id);

    /**
     * 用户加入活动
     *
     * @param userId
     * @param activityId
     */
    void join(Long userId, Long activityId);

    /**
     * 根据用户id和选择条件获取活动集合
     *
     * @param userId
     * @param category
     * @param status
     * @param currentPage
     * @param pageSize
     * @return
     */
    Page<Activity> getListByUserId(Long userId, Integer category, Long status, Long currentPage, Long pageSize);

    /**
     * 删除活动信息
     *
     * @param id
     */
    void deleteById(Long id);
}
