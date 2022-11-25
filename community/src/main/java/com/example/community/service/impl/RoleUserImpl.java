package com.example.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.community.entity.Role;
import com.example.community.entity.RoleUser;
import com.example.community.mapper.RoleMapper;
import com.example.community.mapper.RoleUserMapper;
import com.example.community.service.RoleService;
import com.example.community.service.RoleUserService;
import org.springframework.stereotype.Service;

@Service
public class RoleUserImpl extends ServiceImpl<RoleUserMapper, RoleUser> implements RoleUserService {
}
