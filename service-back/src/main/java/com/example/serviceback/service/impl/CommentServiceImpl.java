package com.example.serviceback.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.serviceback.constant.CacheName;
import com.example.serviceback.mapper.CommentMapper;
import com.example.serviceback.po.Comment;
import com.example.serviceback.service.CommentService;
import com.example.serviceback.util.CacheUtils;
import com.example.serviceback.util.RedisUtils;
import com.example.serviceback.validation.dto.CommentDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 评论表 服务实现类
 * </p>
 *
 * @author CJW
 * @since 2024-04-21
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private CacheUtils cacheUtils;

    @Override
    public Long publishParentComment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setArticleId(commentDTO.getArticleId());
        comment.setContent(commentDTO.getContent());
        comment.setPublishUserId(commentDTO.getPublishUserId());
        comment.setPublishUserName(commentDTO.getPublishUserName());
        comment.setPublishUserAvatar(commentDTO.getPublishUserAvatar());
        commentMapper.insert(comment);
        //更新该帖子的缓存评论数量
        cacheUtils.handleArticleNumberNoExist(comment.getArticleId());
        redisUtils.hIncrBy(CacheName.getArticleNumber(comment.getArticleId()), CacheName.COMMENT_NUMBER_FIELD, 1);
        return comment.getId();
    }

    @Override
    public Long publishSubComment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO, comment);
        commentMapper.insert(comment);
        cacheUtils.handleArticleNumberNoExist(comment.getArticleId());
        redisUtils.hIncrBy(CacheName.getArticleNumber(comment.getArticleId()), CacheName.COMMENT_NUMBER_FIELD, 1);
        return comment.getId();
    }

    @Override
    public void doAgree(Long commentId, Long userId, Boolean up) {
        //判断是否有缓存数据没有查询数据库并加入
        cacheUtils.handleCommentAgreeNoExist(commentId);
        //更新评论点赞数量缓存数据
        if (up) {
            redisUtils.hIncrBy(CacheName.getCommentAgree(),commentId.toString(),1);
            redisUtils.sAdd(CacheName.getUserCommentAgree(userId),commentId.toString());
        } else {
            redisUtils.hIncrBy(CacheName.getCommentAgree(),commentId.toString(),-1);
            redisUtils.sRemove(CacheName.getUserCommentAgree(userId),commentId.toString());
        }
    }

    @Override
    public List<String> getAgreeDataByUserId(Long userId) {
        Set<String> set = redisUtils.setMembers(CacheName.getUserCommentAgree(userId));
        return set.stream().toList();
    }
}
