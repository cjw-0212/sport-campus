package com.example.serviceback.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.serviceback.po.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.serviceback.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author CJW
 * @since 2024-03-25
 */
public interface UserService extends IService<User> {
    /**
     * 用户登陆
     * @param name 用户名
     * @param password 密码
     * @return 用户信息视图对象
     */
    UserVO login(String name, String password);

    /**
     * 用户注册
     * @param name 用户名
     * @param password 密码
     */
    void register(String name,String password);

    /**
     * 获取用户信息分页数据
     * @param currentPage 当前页
     * @param pageSize 每页大小
     * @param name 姓名查询字段
     * @return 用户信息集合
     */

    Page<User> getPage(Long currentPage, Long pageSize, String name);

    /**
     * 更新用户名或个人简介
     * @param id 用户id
     * @param name 用户名
     * @param intro 个人简介
     */
    void updateInfo(Long id, String name, String intro);

    /**
     * 用户修改密码
     * @param id 用户id
     * @param rawPwd 原密码
     * @param newPwd 新密码
     */
    void changePwd(Long id, String rawPwd, String newPwd);

    /**
     * 更新头像
     * @param id 用户id
     * @param file 图片
     * @return 存储的文件名
     */
    String updateAvatar(Long id, MultipartFile file) throws IOException;

    /**
     * 根据id获取用户信息
     * @param id 用户id
     * @return 用户试图对象
     */
    UserVO getInfoById(Long id);
}
