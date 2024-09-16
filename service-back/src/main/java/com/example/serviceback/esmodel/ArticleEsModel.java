package com.example.serviceback.esmodel;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author CJW
 * @since 2024/8/25
 */
@Data
public class ArticleEsModel {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createTime;
    private Integer agreeNumber;
}
