package com.example.community.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.community.common.R;
import com.example.community.entity.Menu;
import com.example.community.entity.MenuRole;
import com.example.community.mapper.MenuRoleMapper;
import com.example.community.service.MenuRoleService;
import com.example.community.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 *  权限菜单接口
 */
@RestController
@Slf4j
@RequestMapping("/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRoleService menuRoleService;

    /**
     * 查询所有菜单树信息
     * @return
     */
    @RequestMapping("/treeList")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public R treeList(){
        List<Menu> menuList = menuService.list(new QueryWrapper<Menu>().orderByAsc("order_num"));
        List<Menu> menuList1 = menuService.buildTreeMenu(menuList);
        return R.success("成功").add("treeMenu",menuList1);
    }

    /**
     * 根据角色id查询权限菜单id
     * @return
     */
    @RequestMapping("/roleid/{id}")
    @PreAuthorize("hasAuthority('system:role:menu')")
    public R roleid(@PathVariable(value = "id")String id){
        List<MenuRole> menuRoleList = menuRoleService.list(new QueryWrapper<MenuRole>().eq("role_id", id));
        List<String> collect = menuRoleList.stream().map(p -> p.getMenuId()).collect(Collectors.toList());
        return R.success("成功").add("memuIdList",collect);
    }


    /**
     *通过角色id修改角色菜单权限
     * @param id
     * @param menuIds
     * @return
     */
    @RequestMapping("/updateMenus/{id}")
    @Transactional
    public R updateMenus(@PathVariable(value = "id")String id,@RequestBody String[] menuIds){
        menuRoleService.remove(new QueryWrapper<MenuRole>().eq("role_id",id));

        List<MenuRole> menuRoleList = new ArrayList<>();
        Arrays.stream(menuIds).forEach(menuId->{
            MenuRole menuRole = new MenuRole();
            menuRole.setRoleId(id);
            menuRole.setMenuId(menuId);
            menuRoleList.add(menuRole);
        });
        System.out.println("----------------------"+menuRoleList);
        menuRoleService.saveBatch(menuRoleList);
        return R.success("成功");
    }


    /**
     * 添加或者修改
     * @param sysMenu
     * @return
     */
    @PostMapping("/save")
    @Transactional
    @PreAuthorize("hasAuthority('system:menu:add')"+"||"+"hasAuthority('system:menu:edit')")
    public R save(@RequestBody Menu Menu){
        if(Menu.getId()==null || Menu.getId().equals("-1")){
            Menu.setCreateTime(new Date());
            Menu.setId(null);
            menuService.save(Menu);
            return R.success("添加成功");
        }else{
            Menu.setUpdateTime(new Date());
            menuService.updateById(Menu);
            return R.success("修改成功");
        }

    }

    /**
         * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public R findById(@PathVariable(value = "id")String id){
        Menu Menu = menuService.getById(id);
        return R.success(Menu);
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    @Transactional
    public R delete(@PathVariable(value = "id")String id){
        int count = menuService.count(new QueryWrapper<Menu>().eq("parent_id", id));
        if(count>0){
            return R.error("请先删除子菜单！");
        }
        menuService.removeById(id);
        return R.success("删除成功");
    }

}
