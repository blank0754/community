package com.example.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.community.entity.MenuRole;
import com.example.community.mapper.MenuRoleMapper;
import com.example.community.service.MenuRoleService;
import org.springframework.stereotype.Service;

@Service
public class MenuRoleImpl extends ServiceImpl<MenuRoleMapper, MenuRole> implements MenuRoleService {
}
