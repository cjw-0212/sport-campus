package com.example.serviceback.config;

import com.example.serviceback.mapper.ArticleMapper;
import com.example.serviceback.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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

}
