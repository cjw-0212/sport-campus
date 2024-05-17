package com.example.serviceback.util;

import com.example.serviceback.constant.CacheName;
import com.example.serviceback.mapper.ArticleMapper;
import com.example.serviceback.mapper.CommentMapper;
import com.example.serviceback.po.Article;
import com.example.serviceback.po.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CJW
 * @since 2024/4/24
 */
@Component
public class CacheUtils {
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private CommentMapper commentMapper;

    /**
     * 处理文章点赞评论数量缓存不存在的情况
     *
     * @param articleId 文章id
     */
    public void handleArticleNumberNoExist(Long articleId) {
        Boolean hasKey = redisUtils.hasKey(CacheName.getArticleNumber(articleId));
        if (hasKey) {
            return;
        }
        Article numberInfo = articleMapper.getNumberInfo(articleId);
        Map<String, String> map = new HashMap<>(2);
        map.put(CacheName.AGREE_NUMBER_FIELD, numberInfo.getAgreeNumber().toString());
        map.put(CacheName.COMMENT_NUMBER_FIELD, numberInfo.getCommentNumber().toString());
        redisUtils.hPutAll(CacheName.getArticleNumber(articleId), map);
    }

    /**
     * 处理评论点赞数量缓存数据不存在的情况
     * @param commentId 评论id
     */
    public void handleCommentAgreeNoExist(Long commentId) {
        Object object = redisUtils.hGet(CacheName.getCommentAgree(), commentId.toString());
        if (object != null) {
            return;
        }
        Comment comment = commentMapper.getAgreeNumberById(commentId);
        redisUtils.hPut(CacheName.getCommentAgree(), commentId.toString(), comment.getAgreeNumber().toString());
    }
}
