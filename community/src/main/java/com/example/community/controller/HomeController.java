package com.example.community.controller;

import com.example.community.common.BaseContext;
import com.example.community.common.R;
import com.example.community.entity.Password;
import com.example.community.entity.Role;
import com.example.community.entity.User;
import com.example.community.service.RoleService;
import com.example.community.service.UserService;
import com.example.community.utils.Jwt;
import com.example.community.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 主页模块
 */
@RestController
@Slf4j
@RequestMapping("/home")
public class HomeController{
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    /**
     * 根据请求头token解析的id查询用户信息
     * @param
     * @return
     */
    @PostMapping("/userInformation")
    public R<User> userInformation(@RequestHeader String token) {
        log.info("开始查询用户信息");
        Claims claims1 = JwtUtils.parseJWT(token);
        //获取id
        String jti = (String) claims1.get("jti");
        return userService.userInformation(jti);

    }


    /**
     * 根据id修改用户信息
     * @param
     * @return
     */
    @PostMapping("/userupdate")
    @CacheEvict(value = "UserRole",allEntries=true)
    public R<String> userUpdate(@RequestBody @Valid User user, @RequestHeader String token) {
        log.info("开始修改用户信息");
        Claims claims1 = JwtUtils.parseJWT(token);

        //获取id
        String jti = (String) claims1.get("jti");
        user.setId(jti);
        return userService.userUpdate(user);

    }


    /**
     * 根据请求头token解析修改用户密码
     * @param
     * @return
     */
    @PostMapping("/passwordupdate")
    public R<String> passwordUpdate(@RequestBody Password password,@RequestHeader String token) {
        log.info("开始修改用户密码");
        Claims claims1 = JwtUtils.parseJWT(token);

        //获取id
        String jti = (String) claims1.get("jti");
        password.setId(jti);
        return userService.passwordUpdate(password);
    }


    /**
     * 根据Role_id查询用户角色
     * @param
     * @return
     */
    @PostMapping("/roleselect")
    public R<Set<Role>> roleSelect(@RequestHeader String token) {
        log.info("开始查询用户角色");
        Claims claims1 = JwtUtils.parseJWT(token);
        //获取id
        String id = (String) claims1.get("jti");
        return roleService.roleSelect(id);
    }

}
