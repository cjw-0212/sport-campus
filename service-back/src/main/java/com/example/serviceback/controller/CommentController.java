package com.example.serviceback.controller;


import com.example.serviceback.service.CommentService;
import com.example.serviceback.util.Result;
import com.example.serviceback.validation.dto.CommentDTO;
import com.example.serviceback.validation.group.PublishParentComment;
import com.example.serviceback.validation.group.PublishSubComment;
import com.example.serviceback.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        List<CommentVO> commentVOList = commentService.getCommentList(articleId);
        return Result.success(commentVOList);
    }

    @DeleteMapping("/{articleId}")
    public Result<Void> delete(@PathVariable Long articleId, @RequestParam Long[] ids) {
        commentService.deleteBatch(Arrays.stream(ids).toList(), articleId);
        return Result.success();
    }

    @PutMapping("/agree/{commentId}/{userId}")
    public Result<Void> agree(@PathVariable Long commentId, @PathVariable Long userId) {
        commentService.doAgree(commentId, userId, true);
        return Result.success();
    }

    @PutMapping("/disagree/{commentId}/{userId}")
    public Result<Void> disagree(@PathVariable Long commentId, @PathVariable Long userId) {
        commentService.doAgree(commentId,userId,false);
        return Result.success();
    }

    @GetMapping("/agree_data/{userId}")
    public Result<List<String>> getAgreeData(@PathVariable Long userId){
        List<String> list=commentService.getAgreeDataByUserId(userId);
        return Result.success(list);
    }
}
