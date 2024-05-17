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
 * 文章媒体资源表
 * </p>
 *
 * @author CJW
 * @since 2024-03-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ArticleMedia implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 资源路径
     */
    private String url;

    /**
     * 所属文章id
     */
    private Long articleId;

}
