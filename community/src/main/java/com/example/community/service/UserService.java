package com.example.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.community.common.R;
import com.example.community.entity.Password;
import com.example.community.entity.User;
import org.springframework.cache.annotation.CacheEvict;


public interface UserService extends IService<User> {


    R<String> login(User user);

    R<String> register(User user);

    //根据ID查询用户信息
    R<User> userInformation(String id);

    //根据ID修改用户信息
    R<String> userUpdate(User user);

    //根据id修改密码
    R<String> passwordUpdate(Password password);

    R<String> outLogin(String id);
}
