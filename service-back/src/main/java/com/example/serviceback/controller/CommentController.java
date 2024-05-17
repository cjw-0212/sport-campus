package com.example.serviceback.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.serviceback.po.Comment;
import com.example.serviceback.service.CommentService;
import com.example.serviceback.util.Result;
import com.example.serviceback.validation.dto.CommentDTO;
import com.example.serviceback.validation.group.PublishParentComment;
import com.example.serviceback.validation.group.PublishSubComment;
import com.example.serviceback.vo.CommentVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 评论表 前端控制器
 * </p>
 *
 * @author CJW
 * @since 2024-04-21
 */
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/publish/parent")
    public Result<String> publishParentComment(@Validated(PublishParentComment.class) @RequestBody CommentDTO commentDTO) {
        Long commentId = commentService.publishParentComment(commentDTO);
        return Result.success(commentId.toString());
    }

    @PostMapping("/publish/sub")
    public Result<String> publishSubComment(@Validated(PublishSubComment.class) @RequestBody CommentDTO commentDTO) {
        Long commentId = commentService.publishSubComment(commentDTO);
        return Result.success(commentId.toString());
    }

    @GetMapping("/list/{articleId}")
    public Result<List<CommentVO>> list(@PathVariable Long articleId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId, articleId);
        List<Comment> list = commentService.list(queryWrapper);
        List<CommentVO> commentVOList = list.stream().map(comment -> {
            CommentVO commentVO = new CommentVO();
            BeanUtils.copyProperties(comment, commentVO);
            commentVO.setCreateTime(comment.getCreateTime().atZone(ZoneId.systemDefault()).toEpochSecond());
            return commentVO;
        }).toList();
        return Result.success(commentVOList);
    }

    @DeleteMapping
    public Result<Void> delete(@RequestParam Long[] ids) {
        commentService.removeByIds(Arrays.stream(ids).toList());
        return Result.success();
    }

    @PutMapping("/agree/{commentId}/{userId}")
    public Result<Void> agree(@PathVariable Long commentId,@PathVariable Long userId){
        commentService.doAgree(commentId,userId,true);
        return Result.success();
    }
    @PutMapping("/disagree/{commentId}/{userId}")
    public Result<Void> disagree(@PathVariable Long commentId,@PathVariable Long userId){
        commentService.doAgree(commentId,userId,false);
        return Result.success();
    }

    @GetMapping("/agree_data/{userId}")
    public Result<List<String>> getAgreeData(@PathVariable Long userId){
        List<String> list=commentService.getAgreeDataByUserId(userId);
        return Result.success(list);
    }
}
