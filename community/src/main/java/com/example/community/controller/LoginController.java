package com.example.community.controller;

import com.example.community.common.BaseContext;
import com.example.community.common.R;
import com.example.community.entity.User;
import com.example.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 登录注册模块
 */
@RestController
@Slf4j
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param user
     * @return
     */
    @PostMapping
    public R<String> login(@RequestBody User user) {
        log.info("开始登录");
        return userService.login(user);

    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @PostMapping("/register")
    public R<String> register(@RequestBody User user) {
        log.info("开始注册");
        return userService.register(user);

    }

    @PostMapping("/outlogin")
    public R<String> outLogin() {
        log.info("退出登录");
        //从线程空间获取id
        String currentId = String.valueOf(BaseContext.getCurrentId());
        return userService.outLogin(currentId);

    }

}
