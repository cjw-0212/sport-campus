package com.example.serviceback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.serviceback.mapper.ActivityUserMapper;
import com.example.serviceback.mapper.CardMapper;
import com.example.serviceback.mapper.UserMapper;
import com.example.serviceback.po.ActivityUser;
import com.example.serviceback.po.Card;
import com.example.serviceback.po.User;
import com.example.serviceback.service.ActivityUserService;
import com.example.serviceback.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户活动关系表 服务实现类
 * </p>
 *
 * @author CJW
 * @since 2024-06-30
 */
@Service
public class ActivityUserServiceImpl extends ServiceImpl<ActivityUserMapper, ActivityUser> implements ActivityUserService {
    @Autowired
    private ActivityUserMapper activityUserMapper;
    @Autowired
    private CardMapper cardMapper;
    @Autowired
    private UserMapper userMapper;
    @Value("${file.requestPrefix}")
    private String mediaRequestPrefix;

    @Override
    public boolean isJoin(Long userId, Long activityId) {
        LambdaQueryWrapper<ActivityUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityUser::getUserId, userId).eq(ActivityUser::getActivityId, activityId);
        ActivityUser activityUser = activityUserMapper.selectOne(queryWrapper);
        return activityUser != null;
    }

    @Override
    public Page<UserVO> getUserList(Long activityId, Long currentPage, Long pageSize) {
        LambdaQueryWrapper<ActivityUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActivityUser::getActivityId, activityId);
        queryWrapper.orderByDesc(ActivityUser::getCreateTime);
        Page<ActivityUser> page = new Page<>(currentPage, pageSize);
        Page<ActivityUser> activityUserPage = activityUserMapper.selectPage(page, queryWrapper);
        List<Long> userIdList = activityUserPage.getRecords().stream().map(ActivityUser::getUserId).toList();
        if (userIdList.size() == 0) {
            return null;
        }
        //用户id与加入时间map
        Map<Long, LocalDateTime> timeMap = activityUserPage.getRecords().stream()
                .collect(Collectors.toMap(ActivityUser::getUserId, ActivityUser::getCreateTime));
        List<User> joinUserList = userMapper.getJoinUserList(userIdList);
        List<UserVO> userVOS = joinUserList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            userVO.setAvatar(mediaRequestPrefix + userVO.getAvatar());
            userVO.setCreateTime(timeMap.get(userVO.getId()).atZone(ZoneId.systemDefault()).toEpochSecond());
            //获取是否打卡
            LambdaQueryWrapper<Card> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Card::getActivityId, activityId)
                    .eq(Card::getUserId, user.getId());
            Card card = cardMapper.selectOne(queryWrapper1);
            userVO.setIsUserCard(card != null ? 1 : 0);
            return userVO;
        }).toList();
        Page<UserVO> userVOPage = new Page<>(currentPage, pageSize);
        BeanUtils.copyProperties(activityUserPage, userVOPage, "records");
        userVOPage.setRecords(userVOS);
        return userVOPage;
    }
}
