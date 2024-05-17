package com.example.serviceback;

import com.example.serviceback.constant.CacheName;
import com.example.serviceback.mapper.ArticleMapper;
import com.example.serviceback.mapper.CommentMapper;
import com.example.serviceback.mapper.UserMapper;
import com.example.serviceback.util.RedisUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author CJW
 * @since 2024/3/21
 */
@SpringBootTest
class ServiceBackApplicationTests {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private RedisUtils redisUtils;

    @Test
    void contextLoads() {

    }
}
