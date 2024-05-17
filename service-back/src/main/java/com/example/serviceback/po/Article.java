package com.example.serviceback.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 体育资料表
 * </p>
 *
 * @author CJW
 * @since 2024-04-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Article implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 发布用户id
     */
    private Long publishUserId;

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
