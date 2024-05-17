package com.example.serviceback.config;

import com.example.serviceback.constant.CacheName;
import com.example.serviceback.mapper.ArticleMapper;
import com.example.serviceback.mapper.CommentMapper;
import com.example.serviceback.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.Set;

/**
 * @author CJW
 * @since 2024/4/13
 */
@EnableScheduling
@Slf4j
@Configuration
public class ScheduleTaskConfig {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private CommentMapper commentMapper;
    /**
     * 每次持久化任务执行完之后间隔五分钟持久化文章的点赞评论数量信息到数据库并清理缓存
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void persistArticleNumberData() {
        //获取缓存的数量信息并持久化
        Set<String> keys = redisUtils.keys(CacheName.getArticleNumber("*"));
        if (keys.size() == 0) {
            return;
        }
        keys.forEach(key -> {
            Long articleId = Long.parseLong(key.substring(key.lastIndexOf(":") + 1));
            Map<Object, Object> map = redisUtils.hGetAll(key);
            Integer agreeNumber = Integer.valueOf(map.get(CacheName.AGREE_NUMBER_FIELD).toString());
            Integer commentNumber = Integer.valueOf(map.get(CacheName.COMMENT_NUMBER_FIELD).toString());
            articleMapper.setNumberInfo(articleId, agreeNumber, commentNumber);
            //数据插入成功删除缓存
            redisUtils.delete(key);
        });
        log.info("文章点赞数与评论数缓存数据已持久化");
    }

    /**
     * 每次持久化任务执行完之后间隔5分钟持久化文章的点赞评论数量信息到数据库并清理缓存
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void persistCommentAgreeData() {
        Set<Object> fields = redisUtils.hKeys(CacheName.getCommentAgree());
        if (fields.size()==0){
            return;
        }
        fields.forEach(field -> {
            String stringField = field.toString();
            Integer agreeNumber = Integer.valueOf(redisUtils.hGet(CacheName.getCommentAgree(), stringField).toString());
            commentMapper.setAgreeNumberById(Long.valueOf(stringField),agreeNumber);
            redisUtils.hDelete(CacheName.getCommentAgree(),stringField);
        });
        log.info("评论点赞数量缓存数据已持久化");
    }
}
