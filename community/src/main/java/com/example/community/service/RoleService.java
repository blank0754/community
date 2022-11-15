package com.example.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.community.common.R;
import com.example.community.entity.Password;
import com.example.community.entity.Role;
import com.example.community.entity.User;
import org.springframework.cache.annotation.Cacheable;


public interface RoleService extends IService<Role> {


    R<Role> roleSelect(String id);
}
