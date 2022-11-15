package com.example.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.community.common.R;
import com.example.community.entity.Role;
import com.example.community.mapper.RoleMapper;
import com.example.community.service.RoleService;
import com.example.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service

public class RoleServicelmpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    /**
     * 根据Role_id查询用户角色
     * @param
     * @return
     */
    @Override
    @Cacheable(value = "roleCache",key = "#id")
    public R<Role> roleSelect(String id){
        Role byId = roleService.getById(id);
        return R.success(byId);
    }

}
