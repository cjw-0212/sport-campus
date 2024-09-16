package com.example.serviceback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.serviceback.constant.CacheName;
import com.example.serviceback.mapper.CommentMapper;
import com.example.serviceback.po.Comment;
import com.example.serviceback.service.CommentService;
import com.example.serviceback.util.CacheDataUtils;
import com.example.serviceback.util.RedisUtils;
import com.example.serviceback.validation.dto.CommentDTO;
import com.example.serviceback.vo.CommentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
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
    private CacheDataUtils cacheDataUtils;
    @Value("${file.requestPrefix}")
    private String mediaRequestPrefix;

    @Override
    public Long publishParentComment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setArticleId(commentDTO.getArticleId());
        comment.setContent(commentDTO.getContent());
        comment.setPublishUserId(commentDTO.getPublishUserId());
        comment.setPublishUserName(commentDTO.getPublishUserName());
        comment.setPublishUserAvatar(commentDTO.getPublishUserAvatar());
        commentMapper.insert(comment);
        //记录用户交互过的文章id
        redisUtils.sAdd(CacheName.getUserArticleConcern(comment.getPublishUserId()), comment.getArticleId().toString());
        //记录文章被哪些用户交互过
        redisUtils.sAdd(CacheName.getArticleConcernUser(comment.getArticleId()), comment.getPublishUserId().toString());
        //更新该帖子的缓存评论数量
        cacheDataUtils.ensureArticleCommentNumCache(comment.getArticleId());
        redisUtils.hIncrBy(CacheName.getArticleCommentNumber(), comment.getArticleId().toString(), 1);
        return comment.getId();
    }

    @Override
    public Long publishSubComment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO, comment);
        commentMapper.insert(comment);
        //记录用户交互过的文章id
        redisUtils.sAdd(CacheName.getUserArticleConcern(comment.getPublishUserId()), comment.getArticleId().toString());
        //记录文章被哪些用户交互过
        redisUtils.sAdd(CacheName.getArticleConcernUser(comment.getArticleId()), comment.getPublishUserId().toString());
        cacheDataUtils.ensureArticleCommentNumCache(comment.getArticleId());
        redisUtils.hIncrBy(CacheName.getArticleCommentNumber(), comment.getArticleId().toString(), 1);
        return comment.getId();
    }

    @Override
    public void doAgree(Long commentId, Long userId, Boolean up) {
        //判断是否有缓存数据没有查询数据库并加入
        cacheDataUtils.ensureCommentAgreeCache(commentId);
        //更新评论点赞数量缓存数据
        if (up) {
            redisUtils.hIncrBy(CacheName.getCommentAgreeNumber(), commentId.toString(), 1);
            redisUtils.sAdd(CacheName.getUserCommentAgree(userId), commentId.toString());
        } else {
            redisUtils.hIncrBy(CacheName.getCommentAgreeNumber(), commentId.toString(), -1);
            redisUtils.sRemove(CacheName.getUserCommentAgree(userId), commentId.toString());
        }
    }

    @Override
    public List<String> getAgreeDataByUserId(Long userId) {
        Set<String> set = redisUtils.setMembers(CacheName.getUserCommentAgree(userId));
        return set.stream().toList();
    }

    @Override
    public void deleteBatch(List<Long> ids, Long articleId) {
        this.removeByIds(ids);
        redisUtils.hIncrBy(CacheName.getArticleCommentNumber(), articleId.toString(), -ids.size());
    }

    @Override
    public List<CommentVO> getCommentList(Long articleId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId, articleId);
        queryWrapper.orderByDesc(Comment::getAgreeNumber);
        queryWrapper.orderByDesc(Comment::getCreateTime);
        List<Comment> list = this.list(queryWrapper);
        return list.stream().map(comment -> {
            CommentVO commentVO = new CommentVO();
            BeanUtils.copyProperties(comment, commentVO);
            commentVO.setCreateTime(comment.getCreateTime().atZone(ZoneId.systemDefault()).toEpochSecond());
            commentVO.setPublishUserAvatar(mediaRequestPrefix + comment.getPublishUserAvatar());
            return commentVO;
        }).toList();
    }
}
