package com.example.serviceback.service;

import com.example.serviceback.esmodel.ArticleEsModel;
import com.example.serviceback.po.Article;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author CJW
 * @since 2024/8/25
 */
public interface EsService {
    /**
     * 保存文章信息到es
     *
     * @param article
     */
    void saveArticle(Article article) throws IOException;

    /**
     * 全文检索文章
     * @param currentPage
     * @param pageSize
     * @param title
     * @return
     * @throws IOException
     */
    List<ArticleEsModel> searchArticle(Long currentPage, Long pageSize, String title) throws IOException;

    /**
     * 更新文章的点赞数量
     * @param numMap map<articleId,agreeNum>
     * @throws IOException
     */
    void updateAgreeNumber(Map<Object,Object> numMap) throws IOException;
}
