package com.example.serviceback.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author CJW
 * @since 2024-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id(学号)
     */
    private Long id;

    /**
     * 登陆密码
     */
    private String password;

    /**
     * 昵称
     */
    private String name;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 性别(0-未知，1-男生,2-女生)
     */
    private Integer sex;

    /**
     * 出生日期
     */
    private String birthday;

    /**
     * 个人简介
     */
    private String intro;

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
