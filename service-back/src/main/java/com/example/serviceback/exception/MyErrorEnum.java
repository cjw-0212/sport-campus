package com.example.serviceback.exception;

/**
 * @author CJW
 * @since 2024/3/25
 */
public enum MyErrorEnum {
    /**
     * 异常枚举
     */
    USERNAME_PASSWORD_NOT_MATCH("用户名或密码错误"),
    USERNAME_REPEAT("用户名已存在"),
    FILE_EMPTY("文件为空"),
    RAW_PASSWORD_WRONG("原密码错误")
    ;
    private final String message;
    MyErrorEnum(String message){
        this.message=message;
    }
    public String getMessage(){
        return message;
    }
}
