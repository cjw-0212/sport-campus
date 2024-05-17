package com.example.serviceback.mapper;

import com.example.serviceback.po.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

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
     * 获取全部文章的点赞和评论数量
     * @param id 文章id
     * @return
     */
    @Select("select agree_number,comment_number from article where id=#{id} ;")
    Article getNumberInfo(@Param("id") Long id);

    /**
     * 更新文章的点赞和评论数量
     * @param articleId 文章id
     * @param agreeNumber 点赞数量
     * @param commentNumber 评论数量
     */
    @Update("update article set agree_number=#{agreeNumber},comment_number=#{commentNumber} where id=#{articleId};")
    void setNumberInfo(@Param("articleId") Long articleId, @Param("agreeNumber") Integer agreeNumber,
                       @Param("commentNumber") Integer commentNumber);
}
