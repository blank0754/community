package com.example.community.common.security;

import cn.hutool.json.JSONUtil;
import com.example.community.common.BaseContext;
import com.example.community.common.R;
import com.example.community.utils.Jwt;
import com.example.community.utils.JwtUtils;
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
import java.util.concurrent.TimeUnit;

/**
 * 登录成功处理器
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private StringRedisTemplate redisTemplate;

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


        outputStream.write(JSONUtil.toJsonStr(R.success(token)).getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
