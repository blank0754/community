package com.example.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.community.common.BaseContext;
import com.example.community.common.R;
import com.example.community.dto.RoleDto;
import com.example.community.entity.Role;
import com.example.community.entity.RoleUser;
import com.example.community.entity.User;
import com.example.community.service.RoleService;
import com.example.community.service.RoleUserService;
import com.example.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色模块
 */
@RestController
@Slf4j
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleUserService roleUserService;

    /**
     * 角色表分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);

        //1.构造分页构造器
        Page<Role> pageinfo = new Page(page,pageSize);
        Page<RoleDto> roleDtoPage = new Page<>();

        //2.构造条件构造器
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper();
        //添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Role::getRoleName,name);//name不等于空时执行查询

        //排序条件
        queryWrapper.orderByDesc(Role::getCreateTime);//排序条件是创建时间降序

        //3.执行查询
        roleService.page(pageinfo, queryWrapper);//传入分页构造器和条件构造器

        //对象拷贝
        BeanUtils.copyProperties(pageinfo, roleDtoPage, "records");

        List<Role> records = pageinfo.getRecords();
        List<RoleDto> list = records.stream().map((item) -> {//遍历records对象
            RoleDto dishDto = new RoleDto();
            //根据页面提交的用户名username查询数据库
            LambdaQueryWrapper<User> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(User::getRoleId,item.getId());
            List<User> list1 = userService.list(queryWrapper1);

            dishDto.setUser(list1);

            BeanUtils.copyProperties(item, dishDto);



            return dishDto;

        }).collect(Collectors.toList());

        Page<RoleDto> RoleDtoPage1 = roleDtoPage.setRecords(list);
        //执行查询
        return R.success(RoleDtoPage1);
    }

    /**
     * 角色修改
     */
    @PostMapping("/update")
    @CacheEvict(value = "roleCache",key = "#role.id")
    @Transactional
    public R<String> roleUpdate(@RequestBody Role role) {
        log.info("开始修改角色");
        roleService.updateById(role);
        return R.success("修改成功");
    }

    /**
     * 角色添加
     */
    @PostMapping("/add")
    @CacheEvict(value = "roleCache",allEntries=true)
    public R<String> roleAdd(@RequestBody Role role) {
        log.info("开始添加角色");
        LambdaQueryWrapper<Role> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Role::getRoleName,role.getRoleName());
        List<Role> list = roleService.list(queryWrapper1);
        if (list.size()!=0){
            return R.error("重名");
        }
        role.setCreateTime(new Date());
        roleService.save(role);
        return R.success("修改成功");
    }

    /**
     * 角色删除
     */
    @PostMapping("/delete")
    @Transactional
    @CacheEvict(value = "roleCache",allEntries=true)
    public R<String> delete(Long[] ids) {
        LambdaQueryWrapper<RoleUser> queryWrapper = new LambdaQueryWrapper();
        for (Long id : ids) {
            LambdaQueryWrapper<RoleUser> eq = queryWrapper.eq(RoleUser::getRoleId, id);
            List<RoleUser> list = roleUserService.list(eq);
            if (list.size()!=0){
                return R.error("还有人是该角色,不能删除");
            }
        }
        roleService.removeByIds(Arrays.asList(ids));
        return R.success("删除成功");
    }
    /**
     * 根据id来查询用户信息
     * @param
     * @return
     */
    @PostMapping("/getid")
    public R<Role> getById(@RequestBody Role role){
        log.info("根据id查询员工信息");
        Role byId = roleService.getById(role);
        return R.success(byId);
    }



    /**
     * 查询所有角色（不分页）
     */
    @GetMapping("/listAll")
    public R listAll(){
        HashMap<String,Object> roleHashMap = new HashMap<>();
        List<Role> roleList = roleService.list();
        roleHashMap.put("roleList",roleList);
        return R.success(roleHashMap);
    }

    /**
     * 用户角色授权
     * @param userId
     * @param roleIds
     * @return
     */
    @PostMapping("/grantRole/{userId}")
    @PreAuthorize("hasAuthority('system:user:role')")
    @CacheEvict(value = "UserRole",allEntries=true)
    public R grantRole(@PathVariable("userId") String userId,@RequestBody String[]
            roleIds){
        List<RoleUser> userRoleList=new ArrayList<>();
        Arrays.stream(roleIds).forEach(r -> {
            RoleUser roleUser = new RoleUser();
            roleUser.setRoleId(r);
            roleUser.setUserId(userId);
            userRoleList.add(roleUser);
        });
        roleUserService.remove(new QueryWrapper<RoleUser>
                ().eq("user_id",userId));
        roleUserService.saveBatch(userRoleList);
        return R.success("用户角色授权成功");
    }



}
