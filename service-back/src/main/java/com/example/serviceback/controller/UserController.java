package com.example.serviceback.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.serviceback.po.User;
import com.example.serviceback.service.UserService;
import com.example.serviceback.util.JwtUtils;
import com.example.serviceback.util.Result;
import com.example.serviceback.validation.dto.UserDTO;
import com.example.serviceback.validation.group.RegisterUser;
import com.example.serviceback.validation.group.UpdateUser;
import com.example.serviceback.vo.UserVO;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author CJW
 * @since 2024-03-25
 */
@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@Validated(RegisterUser.class) @RequestBody UserDTO userDTO) {
        userService.register(userDTO.getName(), userDTO.getPassword());
        return Result.success();
    }

    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody UserDTO userDTO) {
        UserVO userVO = userService.login(userDTO.getName(), userDTO.getPassword());
        //分发token
        Map<String, Object> claim = new HashMap<>(1);
        claim.put("id", userVO.getId());
        String token = JwtUtils.createJwt(claim);
        Result<UserVO> result = Result.success(userVO);
        result.setMessage(token);
        return result;
    }

    @PutMapping("/{id}/change-pwd")
    public Result<Void> changePwd(@PathVariable Long id,
                                  @RequestParam String rawPwd,
                                  @Length(min = 6, max = 16, message = "密码长度应改为6-16位")
                                  @RequestParam String newPwd) {
        userService.changePwd(id, rawPwd, newPwd);
        return Result.success();
    }

    @GetMapping("/info/{id}")
    public Result<UserVO> getInfoById(@PathVariable Long id) {
        UserVO userVO=userService.getInfoById(id);
        return Result.success(userVO);
    }

    @PutMapping("/update")
    public Result<Void> update(@Validated(UpdateUser.class) @RequestBody UserDTO userDTO) {
        userService.updateInfo(userDTO.getId(), userDTO.getName(), userDTO.getIntro());
        return Result.success();
    }

    @GetMapping("/page")
    public Result<Page<User>> page(@RequestParam Long currentPage, @RequestParam Long pageSize,
                                   @RequestParam(required = false) String name) {
        Page<User> userPage = userService.getPage(currentPage, pageSize, name);
        return Result.success(userPage);
    }

    @PostMapping("/{id}/avatar")
    public Result<String> avatar(@PathVariable Long id, @RequestParam MultipartFile avatar) throws IOException {
        String fileName = userService.updateAvatar(id, avatar);
        return Result.success(fileName);
    }

    @PutMapping("/changeInfo")
    public Result<Void> changeInfo(@RequestBody UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        userService.updateById(user);
        return Result.success();
    }
}
