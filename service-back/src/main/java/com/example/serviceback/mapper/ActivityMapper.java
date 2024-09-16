package com.example.serviceback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.serviceback.po.Activity;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 体育活动表 Mapper 接口
 * </p>
 *
 * @author CJW
 * @since 2024-06-30
 */
@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

}
