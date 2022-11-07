package com.example.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.community.common.R;
import com.example.community.entity.User;


public interface UserService extends IService<User> {


    R<String> login(User user);
}
