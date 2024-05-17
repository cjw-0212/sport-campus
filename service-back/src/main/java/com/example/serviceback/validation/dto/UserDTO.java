package com.example.serviceback.validation.dto;

import com.example.serviceback.validation.group.RegisterUser;
import com.example.serviceback.validation.group.UpdateUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author CJW
 * @since 2024/4/12
 */
@Data
public class UserDTO {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户名
     */
    @Length(max = 8, message = "用户名长度不能超过10位", groups = {RegisterUser.class, UpdateUser.class})
    @NotBlank(message = "用户名不能为空", groups = {RegisterUser.class, UpdateUser.class})
    private String name;

    /**
     * 登陆密码
     */
    @Length(min = 6, max = 16, message = "密码长度应改为6-16位", groups = RegisterUser.class)
    private String password;

    /**
     * 个人简介
     */
    @Length(max = 200, message = "个人简介长度不超过两百字", groups = UpdateUser.class)
    private String intro;
}
