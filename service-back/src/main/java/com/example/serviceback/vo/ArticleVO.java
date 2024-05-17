package com.example.serviceback.vo;

import lombok.Data;

import java.util.List;

/**
 * @author CJW
 * @since 2024/4/14
 */
@Data
public class ArticleVO {
    /**
     * 文章id
     */

    private Long id;
    /**
     * 发布用户id
     */

    private Long publishUserId;
    /**
     * 发布用户名
     */
    private String publishUserName;
    /**
     * 发布用户头像
     */
    private String avatar;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 点赞数
     */
    private Integer agreeNumber;
    /**
     * 评论数
     */
    private Integer commentNumber;
    /**
     * 初始创建时间时间戳
     */

    private Long createTime;
    /**
     * 媒体资源路径集合
     */
    private List<String> medias;
}
