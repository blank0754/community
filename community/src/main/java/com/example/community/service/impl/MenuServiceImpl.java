package com.example.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.community.entity.Menu;
import com.example.community.mapper.MenuMapper;
import com.example.community.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<Menu> buildTreeMenu(List<Menu> menuList) {
        List<Menu> resurltMenuList = new ArrayList<>();

        //寻找子节点
        for (Menu menu:menuList){
            for (Menu menu1:menuList) {
                if (menu1.getParentId().equals(menu.getId())){
                    menu.getChildren().add(menu1);
                }
            }
            if (menu.getParentId().equals("0")){
                resurltMenuList.add(menu);
            }




        }

        return resurltMenuList;
    }
}
