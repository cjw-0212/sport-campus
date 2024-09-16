package com.example.serviceback.util;

import com.example.serviceback.constant.CacheName;
import com.example.serviceback.mapper.ArticleMapper;
import com.example.serviceback.mapper.CommentMapper;
import com.example.serviceback.po.Article;
import com.example.serviceback.po.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author CJW
 * @since 2024/4/24
 */
@Component
public class CacheDataUtils {
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 处理文章点赞数量缓存不存在的情况
     *
     * @param articleId 文章id
     */
    public void ensureArticleAgreeNumCache(Long articleId) {
        boolean hasAgreeNumberKey = redisUtils.hExists(CacheName.getArticleAgreeNumber(), articleId.toString());
        if (hasAgreeNumberKey) {
            //缓存数据存在返回
            return;
        }
        //数据不存在或不完整，查询数据并进行缓存补充
        Article article = articleMapper.getAgreeNumber(articleId);
        redisUtils.hPut(CacheName.getArticleAgreeNumber(), articleId.toString(),
                article.getAgreeNumber().toString());
    }

    /**
     * 处理文章评论数量缓存不存在的情况
     *
     * @param articleId 文章id
     */
    public void ensureArticleCommentNumCache(Long articleId) {
        boolean hasCommentNumberKey = redisUtils.hExists(CacheName.getArticleCommentNumber(), articleId.toString());
        if (hasCommentNumberKey) {
            //缓存数据存在返回
            return;
        }
        //数据不存在或不完整，查询数据并进行缓存补充
        Article article = articleMapper.getCommentNumber(articleId);
        redisUtils.hPut(CacheName.getArticleCommentNumber(), articleId.toString(),
                article.getCommentNumber().toString());

    }

    /**
     * 将列表内的文章点赞评论数量信息缓存
     *
     * @param articleList
     */
    public void cacheArticleNumberData(List<Article> articleList) {
        Map<String, String> agreeNumberMap = new HashMap<>();
        Map<String, String> commentNumberMap = new HashMap<>();
        for (Article article : articleList) {
            agreeNumberMap.put(article.getId().toString(), article.getAgreeNumber().toString());
            commentNumberMap.put(article.getId().toString(), article.getCommentNumber().toString());
        }
        redisUtils.hPutAll(CacheName.getArticleAgreeNumber(), agreeNumberMap);
        redisUtils.hPutAll(CacheName.getArticleCommentNumber(), commentNumberMap);
    }

    /**
     * 处理评论点赞数量缓存数据不存在的情况
     *
     * @param commentId 评论id
     */
    public void ensureCommentAgreeCache(Long commentId) {
        boolean hasAgreeNumberKey = redisUtils.hExists(CacheName.getCommentAgreeNumber(), commentId.toString());
        if (hasAgreeNumberKey) {
            return;
        }
        Comment comment = commentMapper.getAgreeNumberById(commentId);
        redisUtils.hPut(CacheName.getCommentAgreeNumber(), commentId.toString(), comment.getAgreeNumber().toString());
    }

    public void setUserInterData(Long articleId,Long userId){
        Set<String> includeUserIds = stringRedisTemplate.opsForSet().members(CacheName.getArticleConcernUser(articleId));
        List<Object> list = stringRedisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String hashKey = CacheName.getUserInter(userId);
                for (String id : includeUserIds) {
                    if (userId.toString().equals(id)) {
                        continue;
                    }
                    operations.opsForHash().putIfAbsent(hashKey, id, "0");
                    operations.opsForHash().increment(hashKey, id, 1);
                    String includeHashKey = CacheName.getUserInter(id);
                    operations.opsForHash().putIfAbsent(includeHashKey, userId.toString(), "0");
                    operations.opsForHash().increment(includeHashKey, userId.toString(), 1);
                }
                return null;
            }
        });
    }
    public void removeUserInterData(Long articleId,Long userId){
        Set<String> includeUserIds = stringRedisTemplate.opsForSet().members(CacheName.getArticleConcernUser(articleId));
        List<Object> list = stringRedisTemplate.executePipelined(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String hashKey = CacheName.getUserInter(userId);
                for (String id : includeUserIds) {
                    if (userId.toString().equals(id)) {
                        continue;
                    }
                    operations.opsForHash().increment(hashKey, id, -1);
                    String includeHashKey = CacheName.getUserInter(id);
                    operations.opsForHash().increment(includeHashKey, userId.toString(), -1);
                }
                return null;
            }
        });
    }
}
