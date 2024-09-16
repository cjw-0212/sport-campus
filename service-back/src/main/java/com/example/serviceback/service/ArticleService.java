package com.example.serviceback.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.serviceback.po.Article;
import com.example.serviceback.validation.dto.ArticleDTO;
import com.example.serviceback.vo.ArticleVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 体育资料表 服务类
 * </p>
 *
 * @author CJW
 * @since 2024-03-29
 */
public interface ArticleService extends IService<Article> {

    /**
     * 保存文章信息
     *
     * @param articleDTO 数据传输对象
     * @param files      媒体资源
     * @return
     * @throws IOException
     */
    Long saveArticle(ArticleDTO articleDTO);

    /**
     * 删除文章
     *
     * @param id 文章id
     */
    void deleteArticle(Long id);

    /**
     * 获取文章分页数据
     *
     * @param currentPage 当前页
     * @param pageSize    每页大小
     * @return 文章数据分页对象
     */
    Page<ArticleVO> getPage(Long currentPage, Long pageSize,Long userId);

    /**
     * 用户点赞或取消点赞文章
     *
     * @param articleId 文章id
     * @param userId    用户id
     * @param up        增加或减少
     */
    void doAgree(Long articleId, Long userId, Boolean up);

    /**
     * 获取用户是否点赞过对应文章的数据
     *
     * @param userId     用户id
     * @return 点赞过的文章集合
     */
    List<String> getAgreeDataByUserId(Long userId);

    /**
     * 保存文章的媒体资源
     *
     * @param articleId 文章id
     * @param file      文件
     * @throws IOException
     */
    void saveMedia(Long articleId, MultipartFile file) throws IOException;

    /**
     * 根据用户id和分类获取文章列表
     *
     * @param userId
     * @param category
     * @param currentPage
     * @param pageSize
     * @return
     */
    Page<ArticleVO> getPageByUserId(Long userId, Integer category, Long currentPage, Long pageSize);

    /**
     * 全文检索
     * @param currentPage
     * @param pageSize
     * @param title
     * @return
     */
    List<ArticleVO> searchByKeyword(Long currentPage, Long pageSize, String title) throws IOException;


    void prepareRecommendData(Object targetUserId);
}
