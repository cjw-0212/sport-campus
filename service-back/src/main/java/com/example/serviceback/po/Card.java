package com.example.serviceback.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 打卡记录表
 * </p>
 *
 * @author CJW
 * @since 2024-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Card implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 所属用户id
     */
    private Long userId;

    /**
     * 所属活动id
     */
    private Long activityId;

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
