package com.example.serviceback.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.serviceback.service.ArticleService;
import com.example.serviceback.util.Result;
import com.example.serviceback.validation.dto.ArticleDTO;
import com.example.serviceback.vo.ArticleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 体育资料表 前端控制器
 * </p>
 *
 * @author CJW
 * @since 2024-03-29
 */
@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @PostMapping("/publish/text")
    public Result<Long> publish(@Validated @RequestBody ArticleDTO articleDTO){
        Long articleId=articleService.saveArticle(articleDTO);
        return Result.success(articleId);
    }

    @PostMapping("/publish/media/{articleId}")
    public Result<Void> publishMediaById(@PathVariable Long articleId,@RequestParam MultipartFile file) throws IOException {
        articleService.saveMedia(articleId,file);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<Page<ArticleVO>> page(@RequestParam Long currentPage, @RequestParam Long pageSize,
                                        @RequestParam(required = false) String title) {
        Page<ArticleVO> articleVoPage = articleService.getPage(currentPage, pageSize, title);
        return Result.success(articleVoPage);
    }

    @PutMapping("/agree/{articleId}/{userId}")
    public Result<Void> agree(@PathVariable Long articleId, @PathVariable Long userId) {
        articleService.doAgree(articleId, userId, true);
        return Result.success();
    }

    @PutMapping("/disagree/{articleId}/{userId}")
    public Result<Void> disagree(@PathVariable Long articleId, @PathVariable Long userId) {
        articleService.doAgree(articleId, userId, false);
        return Result.success();
    }

    @GetMapping("/agree_data/{userId}")
    public Result<List<String>> agreeData(@PathVariable Long userId) {
        List<String> agreeList = articleService.getAgreeDataByUserId(userId);
        return Result.success(agreeList);
    }
}
