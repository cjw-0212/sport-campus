package com.example.serviceback.config;

import com.example.serviceback.constant.CacheName;
import com.example.serviceback.mapper.ArticleMapper;
import com.example.serviceback.po.Article;
import com.example.serviceback.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CJW
 * @since 2024/4/13
 */
@Component
@Slf4j
public class InitConfig {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private RedisUtils redisUtils;

    @PostConstruct
    public void init() {

    }

    /*private void initArticleCacheData() {
        //缓存所有文章的点赞数和评论数
        List<Article> articleList = articleMapper.getNumberInfo();
        Map<String, String> agreeNumberMap = new HashMap<>();
        Map<String, String> commentNumberMap = new HashMap<>();
        for (Article article : articleList) {
            agreeNumberMap.put(article.getId().toString(), article.getAgreeNumber().toString());
            commentNumberMap.put(article.getId().toString(), article.getCommentNumber().toString());
        }
        redisUtils.hPutAll(CacheName.articleAgreeNumber(), agreeNumberMap);
        redisUtils.hPutAll(CacheName.articleCommentNumber(), commentNumberMap);
        log.info("文章点赞数与评论数缓存数据初始化成功");
    }*/
}
