package com.example.serviceback.validation.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author CJW
 * @since 2024/7/21
 */
@Data
public class ActivityDTO {
    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    @Length(max = 20, message = "标题长度不能超过20位")
    private String title;

    /**
     * 活动内容描述
     */
    @NotBlank(message = "活动内容不能为空")
    @Length(max = 200, message = "活动内容描述不能超过200位")
    private String content;

    /**
     * 发布人id
     */
    @NotNull(message = "发布人id不能为空")
    private Long publishUserId;

    /**
     * 总人数
     */
    @Min(value = 1, message = "活动人数不能为0")
    private Integer totalNum;

    /**
     * 详细地址
     */

    private String address;

    /**
     * 地点名称
     */
    @NotBlank(message = "活动位置信息缺失")
    private String addressName;

    /**
     * 活动地点（经度）
     */
    private String longitude;
    /**
     * 活动地点（纬度）
     */

    private String latitude;
    /**
     * 报名截止时间
     */
    @NotBlank(message = "活动开始时间不能为空")
    private String startTime;
}
