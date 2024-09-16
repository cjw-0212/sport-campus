package com.example.serviceback.exception;

/**
 * @author CJW
 * @since 2024/3/25
 */
public enum MyErrorEnum {
    /**
     * 异常枚举
     */
    USERNAME_PASSWORD_NOT_MATCH("账号或密码错误"),
    USERNAME_REPEAT("用户名已存在"),
    FILE_EMPTY("文件为空"),
    RAW_PASSWORD_WRONG("原密码错误"),
    SERVICE_ERROR("服务异常"),
    OUT_OT_ACTIVITY_NUMBER("活动人数已超出"),
    REPEAT_JOIN_ACTIVITY("重复加入活动"),
    ACTIVITY_NOY_IN_CARD("活动未在打卡期间"),
    CARD_NOT_IN_RANGE("打卡位置不在打卡范围内");
    private final String message;

    MyErrorEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
