package com.example.serviceback.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.serviceback.po.Card;
import com.example.serviceback.service.CardService;
import com.example.serviceback.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 打卡记录表 前端控制器
 * </p>
 *
 * @author CJW
 * @since 2024-06-30
 */
@RestController
@RequestMapping("/card")
public class CardController {
    @Autowired
    private CardService cardService;

    @GetMapping("/{activityId}/{userId}")
    public Result<Integer> isCard(@PathVariable Long activityId, @PathVariable Long userId) {
        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Card::getActivityId, activityId).eq(Card::getUserId, userId);
        Card card = cardService.getOne(queryWrapper);
        return Result.success(card != null ? 1 : 0);
    }

    @PostMapping("/{activityId}/{userId}")
    public Result<Void> doCard(@PathVariable Long activityId, @PathVariable Long userId,
                               @RequestParam Double longitude, @RequestParam Double latitude) {
        cardService.doCard(activityId,userId,longitude,latitude);
        return Result.success();
    }
}
