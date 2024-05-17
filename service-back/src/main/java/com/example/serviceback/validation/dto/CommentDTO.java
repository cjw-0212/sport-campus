package com.example.serviceback.validation.dto;

import com.example.serviceback.validation.group.PublishParentComment;
import com.example.serviceback.validation.group.PublishSubComment;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author CJW
 * @since 2024/4/13
 */
@Data
public class CommentDTO {

 /**
  * 父评论id（0表示为一级评论，其他为父级评论的id）
  */
 @NotNull(message = "父级评论id不能为空", groups = PublishSubComment.class)
 private Long parentCommentId;

 /**
  * 所属文章id
  */
 @NotNull(message = "所属文章id不能为空", groups = PublishParentComment.class)
 private Long articleId;

 /**
  * 评论内容
  */
 @NotBlank(message = "评论内容不能为空", groups = {PublishParentComment.class, PublishSubComment.class})
 @Length(max = 500, message = "评论内容不能超过500字", groups = {PublishParentComment.class, PublishSubComment.class})
 private String content;

 /**
  * 发布用户id
  */
 @NotNull(message = "发布者用户id不能为空", groups = {PublishParentComment.class, PublishSubComment.class})
 private Long publishUserId;

 /**
  * 发布用户名
  */
 @Length(max = 10, message = "用户名长度不能超过10位", groups = {PublishParentComment.class, PublishSubComment.class})
 @NotBlank(message = "发布者用户名不能为空", groups = {PublishParentComment.class, PublishSubComment.class})
 private String publishUserName;

 /**
  * 发布者的头像
  */
 private String publishUserAvatar;

 /**
  * 回复用户名
  */
 @Length(max = 10, message = "用户名长度不能超过10位", groups = PublishSubComment.class)
 @NotBlank(message = "被回复者用户名不能为空", groups = PublishSubComment.class)
 private String replyUserName;

 /**
  * 被回复评论id
  */
 @NotNull(message = "被回复的评论id不能为空")
 private Long replyCommentId;
}
