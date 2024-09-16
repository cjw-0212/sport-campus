package com.example.serviceback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.serviceback.po.ActivityUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户活动关系表 Mapper 接口
 * </p>
 *
 * @author CJW
 * @since 2024-06-30
 */
@Mapper
public interface ActivityUserMapper extends BaseMapper<ActivityUser> {
    /**
     * 获取用户加入的活动id
     *
     * @param userId
     * @return
     */
    List<Long> getActivityIdByUserId(@Param("userId") Long userId);
}
