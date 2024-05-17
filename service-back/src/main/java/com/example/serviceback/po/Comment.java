package com.example.serviceback.po;

import java.io.Serial;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 评论表
 * </p>
 *
 * @author CJW
 * @since 2024-04-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Comment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 父评论id（0表示为一级评论，其他为父级评论的id）
     */
    private Long parentCommentId;

    /**
     * 所属文章id
     */
    private Long articleId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 发布用户id
     */
    private Long publishUserId;

    /**
     * 发布用户名
     */
    private String publishUserName;

    /**
     * 发布者的头像
     */
    private String publishUserAvatar;

    /**
     * 回复用户名
     */
    private String replyUserName;

    /**
     * 被回复评论id
     */
    private Long replyCommentId;

    /**
     * 点赞数
     */
    private Integer agreeNumber;

    /**
     * 初始创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 上次更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
