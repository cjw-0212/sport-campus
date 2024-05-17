package com.example.serviceback.mapper;

import com.example.serviceback.po.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 评论表 Mapper 接口
 * </p>
 *
 * @author CJW
 * @since 2024-04-21
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    /**
     * 根据id回去点赞数量
     * @param id 评论id
     * @return
     */
    @Select("select agree_number from comment where id=#{id};")
    Comment getAgreeNumberById(@Param("id") Long id);

    /**
     * 根据id修改点赞数量
     * @param id 评论id
     * @param agreeNumber 新点赞数量
     */
    @Update("update comment set agree_number=#{agreeNumber} where id=#{id};")
    void setAgreeNumberById(@Param("id" )Long id,@Param("agreeNumber") Integer agreeNumber);
}
