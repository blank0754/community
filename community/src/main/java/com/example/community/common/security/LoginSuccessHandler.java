package com.example.community.common.security;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.community.common.BaseContext;
import com.example.community.common.R;
import com.example.community.entity.Menu;
import com.example.community.entity.Role;
import com.example.community.entity.User;
import com.example.community.service.MenuService;
import com.example.community.service.RoleService;
import com.example.community.service.UserService;
import com.example.community.utils.Jwt;
import com.example.community.utils.JwtUtils;
import com.example.community.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 登录成功处理器
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();

        //6.登录成功，先清空redis中的token，再将token存入redis，返回token给前端
        String username=authentication.getName();
        String id = String.valueOf(BaseContext.getCurrentId());
        String token = JwtUtils.genJwtToken(id,username);
        ValueOperations valueOperations = redisTemplate.opsForValue();

        valueOperations.set(id, token, 30, TimeUnit.MINUTES);

        List<Role> roleList = roleService.list(new QueryWrapper<Role>().inSql("id", "select role_id from role_user where user_id=" + id));

        //遍历所有角色获取所有菜单权限 而且不能重复
        //select * from menu WHERE id IN(select menu_id from menu_role where role_id= 2)
        Set<Menu> menuSet = new HashSet<>();
        for (Role role:roleList) {
            List<Menu> menuList = menuService.list(new QueryWrapper<Menu>().inSql("id", "select menu_id from menu_role where role_id=" + role.getId()));
            for (Menu menu:menuList){
                    menuSet.add(menu);
            }
        }
        List<Menu> menuList = new ArrayList<>(menuSet);


        //排序
        menuList.sort(Comparator.comparing(Menu::getOrderNum));


        List<Menu> menuList1 = menuService.buildTreeMenu(menuList);
        System.out.println("menuList1---"+menuList1);

        R<User> userR = userService.userInformation(id);



        outputStream.write(JSONUtil.toJsonStr(R.success(token).add("menuList",menuList1).add("currentUser",userR)).getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
