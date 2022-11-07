package com.example.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.community.common.R;
import com.example.community.entity.User;
import com.example.community.mapper.UserMapper;
import com.example.community.service.UserService;
import com.example.community.utils.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.concurrent.TimeUnit;

@Service

public class UserServicelmpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public R<String> login(User user) {
        //1.将页面提交的密码password进行MD5加密处理
        String password = user.getPassword();
//        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        User userOne = userService.getOne(queryWrapper);//唯一的方法

        //3.如果没有查询到则返回登录失败结果
        if (userOne == null) {
            return R.error("登陆失败，请检查用户名");
        }

        //4.密码比对，如果不一致则返回登录失败结果
        if (!userOne.getPassword().equals(password)) {
            return R.error("登陆失败，请检查密码");
        }

        //5.查看员工状态，如果已禁用状态，则返回员工已禁用结果
        if (userOne.getStatus() == 0) {
            return R.error("该账号已封禁");
        }

        //6.登录成功，先清空redis中的token，再将token存入redis，返回token给前端
        String token = Jwt.getJwtToken(user.getId(), user.getUsername());
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(user.getUsername(), token, 30, TimeUnit.MINUTES);
        return R.success(token);
    }
}



