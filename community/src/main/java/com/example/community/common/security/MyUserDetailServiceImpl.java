package com.example.community.common.security;

import cn.hutool.core.date.DateTime;
import com.example.community.common.BaseContext;
import com.example.community.common.UserCountLockException;
import com.example.community.entity.User;
import com.example.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * UserDetails
 */
@Service
public class MyUserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userService.getByUsername(s);
        System.out.println(user);
        if(user == null){
            throw new UsernameNotFoundException("用户名或密码错误");
        }else if(user.getStatus() == 1){
            throw new UserCountLockException("该用户账号被封禁,具体请联系管理员");
        }


        BaseContext.setCurrentId(Long.valueOf(user.getId()));//调用BaseContext设置id值到线程存储空间
        int i = user.getCount()+1;
        user.setCount(i);
        user.setLoginTime(new Date());
        redisTemplate.delete("userCache::"+user.getId());
        userService.updateById(user);
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),getUserAuthority(user.getId()));
    }

    /**
     * 鉴权
     * @return
     */
    public List<GrantedAuthority> getUserAuthority(String id) {
        String authority = userService.getUserAuthorityInfo(id);
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authority);
    }
}
