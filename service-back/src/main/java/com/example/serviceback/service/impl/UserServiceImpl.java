package com.example.serviceback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.serviceback.constant.CacheName;
import com.example.serviceback.controller.FileController;
import com.example.serviceback.exception.MyErrorEnum;
import com.example.serviceback.exception.MyException;
import com.example.serviceback.mapper.UserMapper;
import com.example.serviceback.po.User;
import com.example.serviceback.service.UserService;
import com.example.serviceback.util.PasswordUtils;
import com.example.serviceback.util.RedisUtils;
import com.example.serviceback.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author CJW
 * @since 2024-03-25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FileController fileController;
    @Value("${file.requestPrefix}")
    private String mediaRequestPrefix;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public UserVO login(String name, String password) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, name);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new MyException(MyErrorEnum.USERNAME_PASSWORD_NOT_MATCH);
        }
        if (!password.equals(user.getPassword())) {
            throw new MyException(MyErrorEnum.USERNAME_PASSWORD_NOT_MATCH);
        }
        //验证通过
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        if (userVO.getAvatar().length() != 0) {
            userVO.setAvatar(mediaRequestPrefix + userVO.getAvatar());
        }
        return userVO;
    }

    @Override
    public void register(String name, String password) {
        //判断用户名是否重复
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getName, name);
        User selectOne = userMapper.selectOne(queryWrapper);
        if (selectOne != null) {
            throw new MyException(MyErrorEnum.USERNAME_REPEAT);
        }
        //密码加密并保存
        User user = new User();
        user.setName(name);
        user.setPassword(PasswordUtils.encode(password));
        userMapper.insert(user);
    }

    @Override
    public Page<User> getPage(Long currentPage, Long pageSize, String name) {
        Page<User> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null && name.length() != 0, User::getName, name);
        Page<User> userPage = userMapper.selectPage(page, queryWrapper);
        userPage.getRecords().forEach(user -> {
            user.setAvatar(mediaRequestPrefix + user.getAvatar());
        });
        return userPage;
    }

    @Override
    public void updateInfo(Long id, String name, String intro) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getName, name);
        User user = userMapper.selectOne(queryWrapper);
        if (user != null) {
            throw new MyException(MyErrorEnum.USERNAME_REPEAT);
        }
        User updateUser = new User();
        updateUser.setId(id);
        updateUser.setName(name);
        updateUser.setIntro(intro);
        userMapper.updateById(updateUser);
    }

    @Override
    public void changePwd(Long id, String rawPwd, String newPwd) {
        //验证旧密码是否匹配
        User user = userMapper.selectById(id);
        boolean match = PasswordUtils.match(rawPwd, user.getPassword());
        if (!match) {
            throw new MyException(MyErrorEnum.RAW_PASSWORD_WRONG);
        }
        //更改新密码
        String newEncodePwd = PasswordUtils.encode(newPwd);
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, id).set(User::getPassword, newEncodePwd);
        this.update(updateWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateAvatar(Long id, MultipartFile file) throws IOException {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, id);
        User user = userMapper.selectOne(queryWrapper);
        String oldAvatar = user.getAvatar();
        String fileName = fileController.upload(file);
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, id).set(User::getAvatar, fileName);
        this.update(updateWrapper);
        //更新完成删除旧的资源
        fileController.deleteFile(oldAvatar);
        return fileName;
    }

    @Override
    public UserVO getInfoById(Long id) {
        User user = this.getById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        if (userVO.getAvatar().length() != 0) {
            userVO.setAvatar(mediaRequestPrefix + userVO.getAvatar());
        }
        return userVO;
    }
}
