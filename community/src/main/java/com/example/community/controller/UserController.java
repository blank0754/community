package com.example.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.community.common.R;
import com.example.community.dto.RoleDto;
import com.example.community.dto.UserDto;
import com.example.community.entity.Role;
import com.example.community.entity.RoleUser;
import com.example.community.entity.User;
import com.example.community.service.RoleService;
import com.example.community.service.RoleUserService;
import com.example.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RoleUserService roleUserService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public final static String DEFAULT_PASSWORD="123456";

    /**
     * 用户表分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @Cacheable(value = "UserRole",key = "#p1.toString() + (#p0 != null ? # p0.toString() : '') + (#p2 != null ? # p2.toString() : '')")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);

        //1.构造分页构造器
        Page<User> pageinfo = new Page(page,pageSize);
        Page<UserDto> userDtoPage = new Page<>();

        //2.构造条件构造器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();
        //添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),User::getUsername,name);//name不等于空时执行查询

        //排序条件
        queryWrapper.orderByDesc(User::getCreateTime);//排序条件是创建时间降序

        //3.执行查询
        userService.page(pageinfo, queryWrapper);//传入分页构造器和条件构造器

        //对象拷贝
        BeanUtils.copyProperties(pageinfo, userDtoPage, "records");

        List<User> records = pageinfo.getRecords();
        System.out.println("records"+records);

        List<UserDto> list = records.stream().map((item) -> {
            UserDto userDto = new UserDto();
            LambdaQueryWrapper<RoleUser> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(RoleUser::getUserId,item.getId());
            List<RoleUser> list2 = roleUserService.list(queryWrapper1);
            Set<Role> list3 = new HashSet<>();
            System.out.println("测试"+list2);
            list2.stream().map((item1) -> {
                LambdaQueryWrapper<Role> queryWrapper2 = new LambdaQueryWrapper<>();
                queryWrapper2.eq(Role::getId, item1.getRoleId());
                Role byId = roleService.getOne(queryWrapper2);
                list3.add(byId);
                System.out.println("listsssssssssssssss"+list3);

                userDto.setRole(list3);
                BeanUtils.copyProperties(item1, userDto);
                System.out.println("11111111111"+userDto);
                return userDto;
            }).collect(Collectors.toList());
            BeanUtils.copyProperties(item,userDto);
            return userDto;
        }).collect(Collectors.toList());


        Page<UserDto> userDtoPage1 = userDtoPage.setRecords(list);
        //执行查询
        return R.success(userDtoPage1);
        }


    /**
     * 根据id来查询用户信息
     * @param
     * @return
     */
        @PostMapping("/getid")
    public R<User> getById(@RequestBody User user){
        log.info("根据id查询员工信息");
        User byId = userService.getById(user);
        return R.success(byId);
    }

    /**
     * 根据id修改用户信息
     * @param
     * @return
     */
    @PostMapping("/update")
    @Transactional
    @CacheEvict(value = "UserRole",allEntries=true)
    public R<String> Update(@RequestBody User user) {
        log.info("开始修改用户信息");
        userService.updateById(user);
        return R.success("修改成功");

    }

    /**
     * 单个或批量禁用启用
     * @param status
     * @param ids
     * @return
     */

    @PostMapping("/status/{status}")
    @Transactional
    @CacheEvict(value = "UserRole",allEntries=true)
    public R<String> updateStatus(
            @PathVariable("status") int status,
            @RequestParam List<Long> ids
    ){
        //批量替换
//        List<Dish> dishes = new ArrayList<>();
        for (Long id : ids) {
            User user = new User();
            user.setStatus(status);
            user.setId(String.valueOf(id));
//            dishes.add(dish);
            userService.updateById(user);
        }

//        dishService.updateBatchById(dishes, dishes.size());
        return R.success("状态修改成功");
    }

    /**
     * 根据id单个或批量删除用户
     *
     * @param ids
     * @return
     */
    @CacheEvict(value = "UserRole",allEntries=true)
    @PostMapping("/delete")
    @Transactional
    public R<String> delete(String[] ids) {
        LambdaQueryWrapper<RoleUser> queryWrapper = new LambdaQueryWrapper();
        log.info("删除用户id为{}", ids);
        for (String id : ids) {
            User byId = userService.getById(id);
            if (byId.getStatus() == 0){
                return R.error("用户未禁用");
            }
            queryWrapper.like(RoleUser::getUserId,id);//name不等于空时执行查询
//            redisTemplate.delete(String.valueOf(id));
            roleUserService.remove(queryWrapper);
        }

        userService.removeByIds(Arrays.asList(ids));
        return R.success("成功");
    }

    /**
     * 重置密码
     */

    @GetMapping("/resetPassword/{id}")
    @CacheEvict(value = "UserRole",allEntries=true)
    @Transactional
    @PreAuthorize("hasAuthority('system:user:resetPwd')")
    public R<String> resetPassword(@PathVariable(value = "id")String id){
        User byId = userService.getById(id);
        byId.setPassword(bCryptPasswordEncoder.encode(DEFAULT_PASSWORD));
        userService.updateById(byId);
        return R.success("修改密码成功");
    }




}
