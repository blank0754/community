package com.example.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.community.common.R;
import com.example.community.entity.Password;
import com.example.community.entity.Role;
import com.example.community.entity.User;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Set;


public interface RoleService extends IService<Role> {


    R<Set<Role>> roleSelect(String id);
}
