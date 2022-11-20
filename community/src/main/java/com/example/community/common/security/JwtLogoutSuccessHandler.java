package com.example.community.common.security;

import cn.hutool.json.JSONUtil;
import com.example.community.common.BaseContext;
import com.example.community.common.R;
import com.example.community.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 自定义退出处理
 */
@Component
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();

        //6 退登成功，清空redis中的token
        String token = httpServletRequest.getHeader("token");
        Claims claims1 = JwtUtils.parseJWT(token);
        //获取id
        String jti = (String) claims1.get("jti");
        System.out.println(claims1.get("jti"));
        redisTemplate.delete(jti);

        outputStream.write(JSONUtil.toJsonStr(R.success("退出成功")).getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
