package com.example.serviceback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.serviceback.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author CJW
 * @since 2024-03-25
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据id获取用户名和头像
     * @param id 用户id
     * @return
     */
    @Select("select name,avatar from user where id=#{id};")
    User getNameAndAvatarById(@Param("id") Long id);
}
