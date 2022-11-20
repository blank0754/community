package com.example.community.common.security;

import com.example.community.common.BaseContext;
import com.example.community.common.UserCountLockException;
import com.example.community.entity.User;
import com.example.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * UserDetails
 */
@Service
public class MyUserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userService.getByUsername(s);
        System.out.println(user);
        if(user == null){
            throw new UsernameNotFoundException("用户名或密码错误");
        }else if("1".equals(user.getStatus())){
            throw new UserCountLockException("该用户账号被封禁,具体请联系管理员");
        }


        BaseContext.setCurrentId(Long.valueOf(user.getId()));//调用BaseContext设置id值到线程存储空间
        int i = user.getCount()+1;
        user.setCount(i);
        user.setLoginTime(LocalDateTime.now());
        userService.updateById(user);
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),getUserAuthority());
    }

    public List<GrantedAuthority> getUserAuthority() {
        return new ArrayList<>();
    }
}
