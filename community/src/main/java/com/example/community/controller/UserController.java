package com.example.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.community.common.R;
import com.example.community.dto.RoleDto;
import com.example.community.entity.Role;
import com.example.community.entity.User;
import com.example.community.service.RoleService;
import com.example.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

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

    /**
     * 用户表分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);

        //1.构造分页构造器
        Page<User> pageinfo = new Page(page,pageSize);


        //2.构造条件构造器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();
        //添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),User::getUsername,name);//name不等于空时执行查询

        //排序条件
        queryWrapper.orderByDesc(User::getCreateTime);//排序条件是创建时间降序

        //3.执行查询
        Page<User> page1 = userService.page(pageinfo, queryWrapper);//传入分页构造器和条件构造器

        return R.success(page1);
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
     * 根据id单个或批量删除菜品
     *
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    @Transactional
    public R<String> delete(Long[] ids) {
        log.info("删除用户id为{}", ids);
        for (Long id : ids) {
            User byId = userService.getById(id);
            if (byId.getStatus() == 1){
                return R.error("用户未禁用");
            }
            redisTemplate.delete(String.valueOf(id));
        }

        userService.removeByIds(Arrays.asList(ids));
        return R.success("成功");
    }
}
