package com.example.serviceback.validation.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author CJW
 * @since 2024/4/12
 */
@Data
public class ArticleDTO {

    /**
     * 发布用户id
     */
    @NotNull(message = "发布人id不能为空")
    private Long publishUserId;


    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    @Length(max = 20, message = "标题长度不能超过20位")
    private String title;

    /**
     * 内容
     */
    @NotBlank(message = "内容不能为空")
    @Length(max = 2000, message = "内容长度不可超过两千字")
    private String content;
}
