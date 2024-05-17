package com.example.serviceback.vo;

import lombok.Data;

/**
 * @author CJW
 * @since 2024/4/14
 */
@Data
public class UserVO {
    /**
     * 主键id
     */

    private Long id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 个人简介
     */
    private String intro;
    /**
     * 性别(0-未知，1-男生,2-女生)
     */
    private Integer sex;

    /**
     * 出生日期
     */
    private String birthday;
}

