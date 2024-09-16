package com.example.serviceback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author CJW
 * @since 2024/8/27
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig {
    /**
     * 与spring的异步注解配合使用的自定义线程池
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(
                16,
                32,
                30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(128),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
