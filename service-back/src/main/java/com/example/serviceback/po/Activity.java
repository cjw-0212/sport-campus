package com.example.serviceback.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 体育活动表
 * </p>
 *
 * @author CJW
 * @since 2024-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;
    /**
     * 标题
     */
    private String title;

    /**
     * 活动内容描述
     */
    private String content;

    /**
     * 发布人id
     */
    private Long publishUserId;
    /**
     * 封面图片
     */
    private String photo;

    /**
     * 总人数
     */
    private Integer totalNum;

    /**
     * 已加入人数
     */
    private Integer joinNum;
    /**
     * 活动开始时间
     */
    private String startTime;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 地点名称
     */
    private String addressName;

    /**
     * 活动地点（经度 纬度）
     */
    private String position;

    /**
     * 是否开启打卡
     */
    private Integer isCard;

    /**
     * 是否结束
     */
    private Integer isFinish;

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
