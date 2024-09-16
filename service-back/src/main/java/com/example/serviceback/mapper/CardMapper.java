package com.example.serviceback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.serviceback.po.Card;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 打卡记录表 Mapper 接口
 * </p>
 *
 * @author CJW
 * @since 2024-06-30
 */
@Mapper
public interface CardMapper extends BaseMapper<Card> {

}
