package com.example.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.community.entity.Menu;

import java.util.List;


public interface MenuService extends IService<Menu> {
    List<Menu> buildTreeMenu(List<Menu> menuList);
}
