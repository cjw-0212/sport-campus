package com.example.serviceback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.serviceback.po.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 体育资料表 Mapper 接口
 * </p>
 *
 * @author CJW
 * @since 2024-04-11
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    /**
     * 获取文章的点赞数量
     *
     * @param id 文章id
     * @return
     */
    @Select("select agree_number from article where id=#{id} ;")
    Article getAgreeNumber(@Param("id") Long id);

    /**
     * 获取文章的评论数量
     *
     * @param id 文章id
     * @return
     */
    @Select("select comment_number from article where id=#{id} ;")
    Article getCommentNumber(@Param("id") Long id);
}
