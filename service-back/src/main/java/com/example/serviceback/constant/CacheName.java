package com.example.serviceback.constant;

/**
 * @author CJW
 * @since 2024/4/13
 */
public class CacheName {
    /**
     * 文章的点赞数量数据(hash)-key固定，field位文章id，value为数量
     */
    private static final String ARTICLE_AGREE_NUMBER = "article:agree:number";
    /**
     * 文章的评论数量数据(hash)-key固定，field位文章id，value为数量
     */
    private static final String ARTICLE_COMMENT_NUMBER = "article:comment:number";
    /**
     * 记录文章被哪些用户交互过（set）
     */
    private static final String ARTICLE_CONCERN_USER_PREFIX = "article:concern:user:";


    /**
     * 用户点赞文章数据(set)-key为用户id，value为该用户点赞过的文章id集合
     */
    private static final String USER_ARTICLE_AGREE_PREFIX = "user:article:agree:";
    /**
     * 用户点赞评论数据(set)-key为用户id，value为该用户点赞的评论id的集合
     */
    private static final String USER_COMMENT_AGREE_PREFIX = "user:comment:agree:";
    /**
     * 记录用户交互过的所有文章的id（set）-key为用户id，value为文章id集合
     */
    private static final String USER_ARTICLE_CONCERN_PREFIX = "user:article:concern:";
    /**
     * 记录用户与对应用户交互的相同文章数量
     */
    private static final String USER_INTER_PREFIX = "user:inter:";


    /**
     * 评论的点赞数量(hash)-key固定，field为commentId，value为点赞数量
     */
    private static final String COMMENT_AGREE_NUMBER = "comment:agree:number";

    /**
     * 推荐列表缓存
     */
    private static final String RECOMMEND_INCLUDE_PREFIX = "recommend:include:";
    private static final String RECOMMEND_EXCLUDE_PREFIX = "recommend:exclude:";
    private static final String RECOMMEND_LOCK_PREFIX = "recommend:lock:";

    public static String getArticleAgreeNumber() {
        return ARTICLE_AGREE_NUMBER;
    }

    public static String getArticleCommentNumber() {
        return ARTICLE_COMMENT_NUMBER;
    }

    public static String getUserArticleAgree(Object userId) {
        return USER_ARTICLE_AGREE_PREFIX + userId.toString();
    }

    public static String getUserCommentAgree(Object userId) {
        return USER_COMMENT_AGREE_PREFIX + userId.toString();
    }

    public static String getCommentAgreeNumber() {
        return COMMENT_AGREE_NUMBER;
    }

    public static String getArticleConcernUser(Object articleId) {
        return ARTICLE_CONCERN_USER_PREFIX + articleId.toString();
    }

    public static String getUserArticleConcern(Object userId) {
        return USER_ARTICLE_CONCERN_PREFIX + userId.toString();
    }

    public static String getUserInter(Object userId) {
        return USER_INTER_PREFIX + userId.toString();
    }

    public static String getRecommendInclude(Object userId) {
        return RECOMMEND_INCLUDE_PREFIX + userId.toString();
    }

    public static String getRecommendExclude(Object userId) {
        return RECOMMEND_EXCLUDE_PREFIX + userId.toString();
    }

    public static String getRecommendLock(Object userId) {
        return RECOMMEND_LOCK_PREFIX + userId.toString();
    }
}
