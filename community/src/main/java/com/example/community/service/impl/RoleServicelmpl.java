package com.example.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.community.common.R;
import com.example.community.entity.Role;
import com.example.community.entity.RoleUser;
import com.example.community.mapper.RoleMapper;
import com.example.community.service.RoleService;
import com.example.community.service.RoleUserService;
import com.example.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service

public class RoleServicelmpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleUserService roleUserService;

    /**
     * 根据Role_id查询用户角色
     * @param
     * @return
     */
    @Override
    @Cacheable(value = "roleCache",key = "#id")
    public R<Set<Role>> roleSelect(String id){
        LambdaQueryWrapper<RoleUser> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(RoleUser::getUserId,id);
        List<RoleUser> list = roleUserService.list(queryWrapper1);
        Set<Role> hashSet = new HashSet();

        for (RoleUser roleUser : list) {

            Role byId1 = roleService.getById(roleUser.getRoleId());
            hashSet.add(byId1);

        }
        return R.success(hashSet);
    }

}
