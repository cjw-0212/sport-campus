package com.example.serviceback.constant;

/**
 * @author CJW
 * @since 2024/4/13
 */
public class CacheName {
    /**
     * 文章的点赞评论数量数据(hash)-key为文章id，field-0为点赞数量，field-1键为评论数量
     */
    private static final String ARTICLE_NUMBER_PREFIX = "article:number:";
    public static final String AGREE_NUMBER_FIELD = "0";
    public static final String COMMENT_NUMBER_FIELD = "1";


    /**
     * 用户点赞文章数据(set)-key为用户id，value为该用户点赞过的文章id集合
     */
    private static final String USER_ARTICLE_AGREE_PREFIX = "user:article:agree:";
    /**
     * 用户点赞评论数据(set)-key为用户id，value为该用户点赞的评论id的集合
     */
    private static final String USER_COMMENT_AGREE_PREFIX = "user:comment:agree:";

    /**
     * 评论的点赞数量(hash)-key固定，field为commentId，value为点赞数量
     */
    private static final String COMMENT_AGREE_PREFIX = "comment:agree";

    public static String getArticleNumber(Object articleId) {
        return ARTICLE_NUMBER_PREFIX + articleId.toString();
    }

    public static String getUserArticleAgree(Object userId) {
        return USER_ARTICLE_AGREE_PREFIX + userId.toString();
    }

    public static String getUserCommentAgree(Object userId) {
        return USER_COMMENT_AGREE_PREFIX + userId.toString();
    }

    public static String getCommentAgree() {
        return COMMENT_AGREE_PREFIX;
    }
}
