package com.example.serviceback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.serviceback.po.Comment;
import com.example.serviceback.validation.dto.CommentDTO;
import com.example.serviceback.vo.CommentVO;

import java.util.List;

/**
 * <p>
 * 评论表 服务类
 * </p>
 *
 * @author CJW
 * @since 2024-04-21
 */
public interface CommentService extends IService<Comment> {
    /**
     * 发布一级评论
     *
     * @param commentDTO 数据
     * @return
     */
    Long publishParentComment(CommentDTO commentDTO);

    /**
     * 发布子评论
     *
     * @param commentDTO 数据
     * @return
     */
    Long publishSubComment(CommentDTO commentDTO);
    /**
     * 用户点赞或取消点赞文章
     *
     * @param commentId 评论id
     * @param userId    用户id
     * @param up        增加或减少
     */
    void doAgree(Long commentId, Long userId, Boolean up);

    /**
     * 获取用户的点赞评论id集合
     *
     * @param userId 用户id
     * @return
     */
    List<String> getAgreeDataByUserId(Long userId);

    /**
     * 删除评论
     *
     * @param ids       评论id
     * @param articleId 所属文章id
     */
    void deleteBatch(List<Long> ids, Long articleId);

    /**
     * 获取文章的评论列表
     *
     * @param articleId 文章id
     * @return
     */
    List<CommentVO> getCommentList(Long articleId);
}
