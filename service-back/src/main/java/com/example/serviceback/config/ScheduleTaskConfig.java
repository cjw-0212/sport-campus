package com.example.serviceback.config;

import com.example.serviceback.constant.CacheName;
import com.example.serviceback.mapper.UserMapper;
import com.example.serviceback.po.Article;
import com.example.serviceback.po.Comment;
import com.example.serviceback.service.ArticleService;
import com.example.serviceback.service.CommentService;
import com.example.serviceback.service.EsService;
import com.example.serviceback.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author CJW
 * @since 2024/4/13
 */
@EnableScheduling
@Slf4j
@Configuration
public class ScheduleTaskConfig {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private CommentService commentService;
    @Autowired
    private EsService esService;
    @Autowired
    private UserMapper userMapper;

    /**
     * 每次持久化任务执行完之后间隔一小时持久化文章的点赞数量信息到数据库并清理缓存
     */
    @Scheduled(fixedDelay = 60 * 60 * 1000)
    public void persistArticleAgreeNum() throws IOException {
        Map<Object, Object> agreeNumMap = redisUtils.hGetAll(CacheName.getArticleAgreeNumber());
        //更新es文章点赞量数据
        esService.updateAgreeNumber(agreeNumMap);
        List<Article> updateList = new ArrayList<>();
        agreeNumMap.forEach((articleId, agreeNum) -> {
            Article article = new Article();
            article.setId(Long.valueOf(articleId.toString()));
            article.setAgreeNumber(Integer.valueOf(agreeNum.toString()));
            updateList.add(article);
        });
        articleService.updateBatchById(updateList);
        //删除缓存
        redisUtils.delete(CacheName.getArticleAgreeNumber());
        log.info("文章点赞数缓存数据已持久化");
    }

    /**
     * 每次持久化任务执行完之后间隔一小时持久化文章的评论数量信息到数据库并清理缓存
     */
    @Scheduled(fixedDelay = 60 * 60 * 1000)
    public void persistArticleCommentNum() {
        Map<Object, Object> commentNumMap = redisUtils.hGetAll(CacheName.getArticleCommentNumber());
        List<Article> updateList = new ArrayList<>();
        commentNumMap.forEach((articleId, commentNum) -> {
            Article article = new Article();
            article.setId(Long.valueOf(articleId.toString()));
            article.setCommentNumber(Integer.valueOf(commentNum.toString()));
            updateList.add(article);
        });
        articleService.updateBatchById(updateList);
        //删除缓存
        redisUtils.delete(CacheName.getArticleCommentNumber());
        log.info("文章评论数缓存数据已持久化");
    }

    /**
     * 每次持久化任务执行完之后间隔一小时持久化评论的点赞数量信息到数据库并清理缓存
     */
    @Scheduled(fixedDelay = 60 * 60 * 1000)
    public void persistCommentAgreeData() {
        Map<Object, Object> agreeNumMap = redisUtils.hGetAll(CacheName.getCommentAgreeNumber());
        List<Comment> updateList = new ArrayList<>();
        agreeNumMap.forEach((commentId, agreeNum) -> {
            Comment comment = new Comment();
            comment.setId(Long.valueOf(commentId.toString()));
            comment.setAgreeNumber(Integer.valueOf(agreeNum.toString()));
            updateList.add(comment);
        });
        commentService.updateBatchById(updateList);
        redisUtils.delete(CacheName.getCommentAgreeNumber());
        log.info("评论点赞数缓存数据已持久化");
    }

    /**
     * 每天的3点触发一次（24小时制）
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void setRecommendData() {
        System.out.println("处理推荐数据");
        List<Long> userIds = userMapper.getAllUserId();
        for (Long userId : userIds) {
            articleService.prepareRecommendData(userId);
        }
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void test() {
        System.out.println("测试处理推荐数据"+ LocalDateTime.now());
    }
}
