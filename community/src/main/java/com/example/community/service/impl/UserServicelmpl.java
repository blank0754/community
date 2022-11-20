package com.example.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.community.common.R;
import com.example.community.entity.Password;
import com.example.community.entity.User;
import com.example.community.mapper.UserMapper;
import com.example.community.service.UserService;
import com.example.community.utils.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class UserServicelmpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //用户登录
    @Override
    public R<String> login(User user) {
        //1.将页面提交的密码password进行MD5加密处理
        String password = user.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

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
        if (userOne.getStatus() == 1) {
            return R.error("该账号已封禁");
        }

        //6.登录成功，先清空redis中的token，再将token存入redis，返回token给前端
        String token = Jwt.getJwtToken(userOne.getId(), userOne.getUsername());
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(userOne.getId(), token, 30, TimeUnit.MINUTES);
        int i = userOne.getCount()+1;
        userOne.setCount(i);
        userOne.setLoginTime(LocalDateTime.now());
        userService.updateById(userOne);
        return R.success(token);
    }

    //用户注册
    @Override
    public R<String> register(User user){

        //根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,user.getUsername());
        User one = userService.getOne(queryWrapper);

        if (one!=null){
            return R.error("用户名重复");

        }else {
//            //设置初始密码,需要进行md5加密处理
//            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
            //进行BCrypt加密
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String newPassword = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(newPassword);
            user.setCreateTime(LocalDateTime.now());//添加注册时间
            user.setCount(0);
            user.setRoleId(0);//设置为普通用户
            userService.save(user);
            return R.success("注册成功");
        }

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

//        userService.save(user);
    }

    //根据ID查询用户信息
    @Override
    @Cacheable(value = "userCache",key = "#id")
    public R<User> userInformation(String id){
        User user = userService.getById(id);
        user.setPassword(null);
        return R.success(user);
    }


    //根据ID修改用户信息
    @Override
    @CacheEvict(value = "userCache",key = "#user.id")
    public R<String> userUpdate(User user){
        User byId = userService.getById(user.getId());
        user.setPassword(byId.getPassword());
        userService.updateById(user);
        return R.success("修改成功");

    }


    //根据id修改密码
    @Override
    public R<String> passwordUpdate(Password password){
        //1.将页面提交的密码password进行MD5加密处理
        String oldpassword = password.getOldpassword();
        String o = DigestUtils.md5DigestAsHex(oldpassword.getBytes());


        User byId = userService.getById(password.getId());
        if (byId.getPassword().equals(o)){
            String newpassword = password.getNewpassword();
            String n = DigestUtils.md5DigestAsHex(newpassword.getBytes());
            byId.setPassword(n);
            userService.updateById(byId);
            return R.success("修改成功");
        }
        return R.error("旧密码不正确");
    }

    //退出登录
    @Override
    public R<String> outLogin(String id){
        redisTemplate.delete(id);
        return R.success("退登成功");

    }

    @Override
    public User getByUsername(String s) {
        //根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,s);
        User one = userService.getOne(queryWrapper);
        return one;
    }

}



